package org.usth.ict.ulake.ingest.model.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper model for configuring a raw http request.
 * */
public class HttpRawRequest {
    public String method;
    public String path;
    public Map<String, List<String>> headers;
    public String body;

    public HttpConfigure conf;

    public HttpRawRequest clone() {
        var newReq = new HttpRawRequest();
        newReq.method = method;
        newReq.path = path;
        newReq.body = body;

        newReq.headers = new HashMap<String, List<String>>();
        for (String k : headers.keySet()) {
            newReq.headers.put(k, new ArrayList<String>());
            newReq.headers.get(k).addAll(headers.get(k));
        }

        newReq.conf = this.conf.clone();
        return newReq;
    }

    public HttpRawRequest(
        String method, String path, Map<String,
        List<String>> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public HttpRawRequest() {
        this.headers = new HashMap<String, List<String>>();
        this.conf = new HttpConfigure();
    }

    public HttpRawRequest setConfig(HttpConfigure conf) {
        this.conf = conf;
        return this;
    }

    public HttpRawRequest addHeader(String key, String value) {
        if (headers.get(key) == null)
            headers.put(key, new ArrayList<String>());
        headers.get(key).add(value);
        return this;
    }

    public HttpRawRequest addPath(String subPath) {
        // TODO: simple implementation without malformed checking
        if (path == null || path.isEmpty()) {
            path = URI.create(subPath).toString();
        } else if (subPath.startsWith("/")) {
            path = URI.create(path + subPath).normalize().toString();
        } else {
            path = URI.create(path + "/" + subPath).normalize().toString();
        }
        return this;
    }
}
