package org.usth.ict.ulake.ingest.model.http;

import java.net.http.HttpClient.Redirect;

/**
 * Configuration class preserved for http client.
 * */
public class HttpConfigure {
    public Redirect redirectStrategy;
    public Integer delayMillsec; // milliseconds delay before send request

    public HttpConfigure() {}

    public HttpConfigure clone() {
        var newConf = new HttpConfigure();
        newConf.redirectStrategy = this.redirectStrategy;
        newConf.delayMillsec = this.delayMillsec;
        return newConf;
    }
}
