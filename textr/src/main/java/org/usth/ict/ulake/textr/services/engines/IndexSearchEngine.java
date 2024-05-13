package org.usth.ict.ulake.textr.services.engines;

import org.jboss.logging.Logger;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;

import java.io.IOException;

@Deprecated
public interface IndexSearchEngine {
    Logger LOG = Logger.getLogger(IndexSearchEngine.class);

    String dataDir = "/home/malenquillaa/tmp/data/cran";
    String indexDir = "/home/malenquillaa/tmp/index";

    IndexResponse index() throws IOException;

    SearchResponse search(String term) throws IOException;
}
