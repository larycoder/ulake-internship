package org.usth.ict.ulake.textr.engine;

import org.jboss.logging.Logger;
import org.usth.ict.ulake.textr.resource.TextrResource;

import java.io.IOException;
import java.util.HashMap;


public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(TextrResource.class);

    int index(RootEngine engine) throws IOException;
    HashMap<String, Float> search(RootEngine engine, String term) throws IOException;
}
