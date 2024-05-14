package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Format supported by Lucene
//- TXT;
//- HTML;
//- RTF;
//- DOCX (Office Open XML – the binary DOC format is not supported);
//- PDF.

@Deprecated
@ApplicationScoped
public class Lucene implements IndexSearchEngine {
    private final Directory indexDirectory;
    private final File dataDirectory;

    private final Analyzer analyzer = new StandardAnalyzer();

    public Lucene() throws IOException {
//        Setup path
        this.indexDirectory = FSDirectory.open(new File(indexDir).toPath());
        this.dataDirectory = new File(dataDir);
    }

    @Override
    public IndexResponse index() throws IOException {
//        Setup indexer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

//        Get data for indexing
        File[] files = dataDirectory.listFiles();
        assert files != null;
        for (final File file : files) {
            LOG.info("Indexing directory: " + file.getAbsolutePath());
            Document doc = new Document();
//            TextField for indexing and StoredField for storing only
            doc.add(new TextField("content", new FileReader(file)));
            doc.add(new StoredField("filename", file.getName()));
//            Create Doc
            indexWriter.addDocument(doc);
        }

        int numIndexed = indexWriter.getDocStats().maxDoc;
        LOG.info("Indexed " + numIndexed + " documents");

        indexWriter.commit();
        indexWriter.close();

        return new IndexResponse(numIndexed);
    }

    @Override
    public SearchResponse search(String queryString) throws IOException {
//        Init response
        SearchResponse searchResponse = new SearchResponse();
        List<DocumentResponse> docs = new ArrayList<>();
//
////        Index if indexDirectory is empty
//        if (indexDirectory.listAll().length == 0)
//            searchResponse.setIndexed(this.index().getIndexed());
//
////        Setup searcher
//        IndexReader indexReader = DirectoryReader.open(indexDirectory);
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//
////        Setup query
//        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
//        Query query = queryBuilder.createPhraseQuery("content", queryString);
//
////        Search process
//        TopDocs topDocs = indexSearcher.search(query, 40);
//        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//
//        for (int i = 0; i < scoreDocs.length; i++) {
//            ScoreDoc scoreDoc = scoreDocs[i];
//            Document doc = indexSearcher.doc(scoreDoc.doc);
//            String filename = doc.get("filename");
//            LOG.info("Searching in: " + filename);
//
//            docs.add(new DocumentResponse(i + 1, filename, dataDir, scoreDoc.score));
//        }
//        searchResponse.setDocs(docs);

        return searchResponse;
    }
}
