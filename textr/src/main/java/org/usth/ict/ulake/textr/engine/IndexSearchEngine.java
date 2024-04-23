package org.usth.ict.ulake.textr.engine;

import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import java.io.IOException;


public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(IndexSearchEngine.class);

    JsonObject index(Root engine) throws IOException;
    JsonObject search(Root engine, String term) throws IOException;
    String getIndexDir();
    String getDataDir();
}
