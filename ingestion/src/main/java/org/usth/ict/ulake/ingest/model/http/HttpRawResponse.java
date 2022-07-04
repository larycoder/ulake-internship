package org.usth.ict.ulake.ingest.model.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Wrapper model for catching raw http response.
 * */
public class HttpRawResponse {
    public Integer statusCode;
    public String uri;
    public Map<String, List<String>> headers;
    public InputStream body;

    public HttpRawResponse() {}

    public void close() {
        try {
            if (body != null)
                body.close();
        } catch (IOException e) { ; }
    }
}
