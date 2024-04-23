package org.usth.ict.ulake.textr.engine;

import io.vertx.core.json.JsonObject;

import java.io.IOException;

public abstract class RootEngine implements IndexSearchEngine{
    JsonObject index() throws IOException {
        return null;
    }
    JsonObject search(String term) throws IOException {
        return null;
    }
    public String getIndexDir() {
        return null;
    }
    public String getDataDir() {
        return null;
    }
}
