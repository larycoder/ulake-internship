package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.util.QueryBuilder;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.IndexFiles;
import org.usth.ict.ulake.textr.models.IndexingStatus;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.repositories.IndexFilesRepository;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LuceneV2 implements IndexSearchEngineV2 {
    
    @Inject
    TikaExtractor tikaExtractor;
    
    @Inject
    LuceneManager luceneManager;
    
    @Autowired
    IndexFilesRepository indexFilesRepo;
    
    @PreDestroy
    protected void preDestroy() throws IOException {
        luceneManager.close();
    }
    
    @Override
    public Document getDocument(String cid, InputStream stream) throws IOException, TikaException {
        String contents = tikaExtractor.extractText(stream);
        return getDocument(cid, contents);
    }
    
    @Override
    public Document getDocument(String cid, String contents) {
        FieldType contentFieldType = new FieldType();
        contentFieldType.setStored(true);
        contentFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        contentFieldType.setStoreTermVectors(true);
        contentFieldType.setStoreTermVectorPositions(true);
        contentFieldType.setStoreTermVectorOffsets(true);
        contentFieldType.freeze();
        
        FieldType cidFieldType = new FieldType();
        cidFieldType.setStored(true);
        cidFieldType.setIndexOptions(IndexOptions.DOCS);
        cidFieldType.setTokenized(false);
        cidFieldType.setOmitNorms(false);
        cidFieldType.freeze();
        
        FieldType metadataFieldType = new FieldType();
        metadataFieldType.setStored(true);
        metadataFieldType.setIndexOptions(IndexOptions.DOCS);
        metadataFieldType.setTokenized(false);
        metadataFieldType.setOmitNorms(false);
        metadataFieldType.freeze();
        
        Document doc = new Document();
        doc.add(new Field(LuceneConstants.CID, cid, cidFieldType));
        // doc.add(new Field(LuceneConstants.NAME, filename, metadataFieldType));
        doc.add(new Field(LuceneConstants.CONTENTS, contents, contentFieldType));
        
        return doc;
    }
    
    @Override
    public IndexResponse indexDoc(Document doc) throws IOException {
        IndexWriter indexWriter = luceneManager.getIndexWriter();
        indexWriter.addDocument(doc);
        
        int numIndexed = indexWriter.getDocStats().numDocs;
        
        return new IndexResponse(numIndexed);
    }
    
    @Override
    public void deleteDoc(String cid) throws IOException {
        IndexWriter indexWriter = luceneManager.getIndexWriter();
        
        indexWriter.deleteDocuments(new Term(LuceneConstants.CID, cid));
        
        luceneManager.maybeMerge();
    }
    
    @Override
    public List<DocumentResponse> searchDoc(String queryString) throws IOException {
        IndexSearcher indexSearcher = luceneManager.getIndexSearcher();
        
        List<DocumentResponse> documents = new ArrayList<>();
        
        try {
            Analyzer analyzer = luceneManager.getAnalyzer();
            
            QueryBuilder queryBuilder = new QueryBuilder(analyzer);
            Query contentsQuery = queryBuilder.createBooleanQuery(LuceneConstants.CONTENTS, queryString);
            // Query nameQuery = queryBuilder
            //         .createBooleanQuery(LuceneConstants.NAME, queryString
            //                 .replace(".", " "));
            
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(contentsQuery, BooleanClause.Occur.SHOULD);
            // builder.add(nameQuery, BooleanClause.Occur.SHOULD);
            BooleanQuery query = builder.build();
            
            TopDocs hits = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
            StoredFields storedFields = indexSearcher.storedFields();
            for (ScoreDoc hit : hits.scoreDocs) {
                Document doc = storedFields.document(hit.doc);
                
                String cid = doc.get(LuceneConstants.CID);
                
                IndexFiles indexFile = indexFilesRepo.findByCoreIdAndStatus(cid, IndexingStatus.STATUS_INDEXED)
                                                     .orElse(null);
                
                if (indexFile != null) {
                    QueryScorer scorer = new QueryScorer(query);
                    Highlighter highlighter = new Highlighter(scorer);
                    
                    String[] highlightContents = highlighter.getBestFragments(analyzer, LuceneConstants.CONTENTS,
                                                                              doc.get(LuceneConstants.CONTENTS),
                                                                              LuceneConstants.MAX_HIGHLIGHT);
                    documents.add(new DocumentResponse(indexFile, hit.score, highlightContents));
                }
            }
        } catch (InvalidTokenOffsetsException e) {
            log.error("Query best fragments failed: ", e);
        } finally {
            luceneManager.releaseIndexSearcher(indexSearcher);
        }
        return documents;
    }
    
    @Override
    public boolean notIndexed(String cid) throws IOException {
        IndexSearcher indexSearcher = luceneManager.getIndexSearcher();
        
        try {
            Query cidQuery = new TermQuery(new Term(LuceneConstants.CID, cid));
            
            TopDocs hits = indexSearcher.search(cidQuery, 1);
            
            if (hits.scoreDocs.length == 0) {
                return true;
            }
        } finally {
            luceneManager.releaseIndexSearcher(indexSearcher);
        }
        return false;
    }
    
    @Override
    public void commit() throws IOException {
        luceneManager.commit();
    }
}
