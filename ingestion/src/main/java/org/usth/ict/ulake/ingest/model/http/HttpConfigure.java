package org.usth.ict.ulake.ingest.model.http;

import java.net.http.HttpClient.Redirect;

/**
 * Configuration class preserved for http client.
 * */
public class HttpConfigure {
    public Redirect redirectStrategy;

    public HttpConfigure() {}
}
