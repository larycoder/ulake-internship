package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.document.Document;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;

import java.io.File;
import java.io.IOException;

public interface IndexSearchEngineV2 {

    Document getDocument(String name, File file) throws IOException;

    Document getDocument(String name, String contents) throws IOException;

    IndexResponse indexDoc(Document doc) throws IOException;

    void deleteDoc(String name) throws IOException;

    SearchResponse searchDoc(String query) throws IOException;
}
