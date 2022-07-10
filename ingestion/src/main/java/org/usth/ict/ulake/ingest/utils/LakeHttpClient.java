package org.usth.ict.ulake.ingest.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import org.usth.ict.ulake.ingest.model.http.HttpConfigure;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;

public class LakeHttpClient {
    public HttpRawRequest req;

    public LakeHttpClient() {}

    public LakeHttpClient(HttpRawRequest req) {
        this.req = req;
    }

    public LakeHttpClient setRequest(HttpRawRequest req) {
        this.req = req;
        return this;
    }

    private HttpClient buildClient(HttpConfigure conf) {
        if (conf == null)
            return HttpClient.newHttpClient();

        var builder = HttpClient.newBuilder();

        if (conf.redirectStrategy != null)
               builder = builder.followRedirects(conf.redirectStrategy);

        return builder.build();
    }

    public HttpRawResponse send() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        BodyPublisher body;

        // path and header
        builder = builder.uri(URI.create(req.path));
        for (String k : req.headers.keySet())
            for (String h : req.headers.get(k))
                builder = builder.header(k, h);

        // body
        if (req.body == null)
            body = BodyPublishers.noBody();
        else
            body = BodyPublishers.ofString(req.body);
        builder = builder.method(req.method, body);

        // client
        HttpClient client = buildClient(req.conf);

        HttpRawResponse lakeResp = null;
        try {
            var resp = client.send(
                           builder.build(), BodyHandlers.ofInputStream());

            lakeResp = new HttpRawResponse();
            lakeResp.statusCode = resp.statusCode();
            lakeResp.uri = resp.uri().toString();
            lakeResp.headers = resp.headers().map();
            lakeResp.body = resp.body();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return lakeResp;
    }

    public static HttpRawResponse send(HttpRawRequest req) {
        return new LakeHttpClient(req).send();
    }
}
