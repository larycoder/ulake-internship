package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IndexSearchEngineV2 {
    
    Logger log = LoggerFactory.getLogger(IndexSearchEngineV2.class.getName());
    
    Document getDocument(String cid, InputStream stream) throws IOException, TikaException;
    
    Document getDocument(String cid, String contents);
    
    IndexResponse indexDoc(Document doc) throws IOException;
    
    void deleteDoc(String cid) throws IOException;
    
    List<DocumentResponse> searchDoc(String query) throws IOException, InvalidTokenOffsetsException;
    
    boolean notIndexed(String cid) throws IOException;
    
    void commit() throws IOException;
}
