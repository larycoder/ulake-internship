package org.usth.ict.ulake.textr.engine;

import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;
import org.usth.ict.ulake.textr.resource.TextrResource;

import java.io.IOException;


public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(IndexSearchEngine.class);

    JsonObject index(RootEngine engine) throws IOException;
    JsonObject search(RootEngine engine, String term) throws IOException;
    String getIndexDir();
    String getDataDir();
}
