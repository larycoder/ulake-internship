package org.usth.ict.ulake.ingest.utils;

import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;


public class RestClientUtil {
    private Client client;

    private String link;
    private String path;
    private int waitingTime = 0; // wait before performing request build

    private Map<String, Object> headerList = new HashMap<>();

    private boolean httpRedirect = false;

    //=======================================//

    public RestClientUtil() {
       setClient();
    }

    private void setClient() {
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
        engine.setFollowRedirects(httpRedirect);
        client = new ResteasyClientBuilderImpl()
                .httpEngine(engine)
                .build();
    }

    public RestClientUtil clone() {
        RestClientUtil instance = new RestClientUtil()
                .setBaseURL(link)
                .setPath(path)
                .setHead(headerList)
                .setHttpRedirect(httpRedirect)
                .setWait(waitingTime);
        return instance;
    }

    public RestClientUtil setHttpRedirect(boolean opt) {
        httpRedirect = opt;
        setClient();
        return this;
    }

    public RestClientUtil setHead(String key, Object value) {
        headerList.put(key, value);
        return this;
    }

    public RestClientUtil setHead(Map<String, Object> heads) {
        if (heads == null) return this;
        for (var key : heads.keySet()) {
            setHead(key, heads.get(key));
        }
        return this;
    }

    public RestClientUtil setBaseURL(String link) {
        this.link = link;
        return this;
    }

    public RestClientUtil setPath(String path) {
        this.path = path;
        return this;
    }

    public RestClientUtil setWait(int time) {
        waitingTime = time;
        return this;
    }

    public Invocation.Builder buildRequest() {
        if (link == null) return null;
        return buildRequest(link);
    }

    public Invocation.Builder buildRequest(String link) {
        if (waitingTime > 0 && waitingTime < 5000) { // 5s is too long
            try {
                Thread.sleep(waitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        WebTarget wt = client.target(link);
        if (path != null) wt = wt.path(path);

        Invocation.Builder builder;
        builder = wt.request();

        // header
        if (headerList != null) {
            for (String key : headerList.keySet()) {
                builder.header(key, headerList.get(key));
            }
        }

        return builder;
    }
}
