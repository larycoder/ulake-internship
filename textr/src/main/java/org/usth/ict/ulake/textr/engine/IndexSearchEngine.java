package org.usth.ict.ulake.textr.engine;

import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import java.io.IOException;


public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(IndexSearchEngine.class);

    String dataDir = "/home/malenquillaa/tmp/data";
    String indexDir = "/home/malenquillaa/tmp/index";

    JsonObject index() throws IOException;
    default JsonObject index(IndexSearchEngine engine) throws IOException {
        return engine.index();
    }

    JsonObject search(String term) throws IOException;
    default JsonObject search(IndexSearchEngine engine, String term) throws IOException {
        return engine.search(term);
    }

    default String getIndexDir() {
        return this.indexDir;
    }
    default String getDataDir() {
        return this.dataDir;
    }
}
