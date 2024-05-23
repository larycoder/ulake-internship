package org.usth.ict.ulake.textr.services;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;
import org.usth.ict.ulake.textr.services.engines.IndexSearchEngineV2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class SearchService {

    @Inject
    IndexSearchEngineV2 indexSearchEngine;

    public SearchResponse search(String query) throws IOException, InvalidTokenOffsetsException {
        return indexSearchEngine.searchDoc(query);
    }
}
