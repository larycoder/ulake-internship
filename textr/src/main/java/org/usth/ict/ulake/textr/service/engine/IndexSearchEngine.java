package org.usth.ict.ulake.textr.service.engine;

import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import java.io.IOException;


public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(IndexSearchEngine.class);

    String dataDir = "/home/malenquillaa/tmp/data";
    String indexDir = "/home/malenquillaa/tmp/index";

    JsonObject index() throws IOException;

    JsonObject search(String term) throws IOException;

    default String getIndexDir() {
        return this.indexDir;
    }
    default String getDataDir() {
        return this.dataDir;
    }
}
