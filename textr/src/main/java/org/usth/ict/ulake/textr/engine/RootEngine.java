package org.usth.ict.ulake.textr.engine;

import io.vertx.core.json.JsonObject;

import java.io.IOException;

public abstract class RootEngine implements IndexSearchEngine{
    int index() throws IOException {
        return 0;
    }
    JsonObject search(String term) throws IOException {
        return null;
    }
}
