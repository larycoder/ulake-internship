package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;
import org.usth.ict.ulake.textr.repositories.DocumentsRepository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LuceneV2 implements IndexSearchEngineV2 {

    @ConfigProperty(name = "textr.documents.indexDir")
    String indexDir;

    private Directory indexDirectory;

    @Autowired
    DocumentsRepository documentsRepository;

    private final Analyzer analyzer = new StandardAnalyzer();

    @PostConstruct
    protected void init() throws IOException {
        this.indexDirectory = FSDirectory.open(new File(indexDir).toPath());
    }

    private IndexWriter getIndexWriter() throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriterConfig.setSimilarity(new BM25Similarity());
        return new IndexWriter(indexDirectory, indexWriterConfig);
    }

    private IndexSearcher getIndexSearcher() throws IOException {
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        return new IndexSearcher(indexReader);
    }

    @Override
    public Document getDocument(String name, File file) throws FileNotFoundException {
        Document doc = new Document();

        doc.add(new StoredField("name", name));
        doc.add(new TextField("contents", new FileReader(file)));

        return doc;
    }

    @Override
    public Document getDocument(String name, String contents) {
        Document doc = new Document();

        doc.add(new StoredField("name", name));
        doc.add(new TextField("contents", contents, Field.Store.YES));

        return doc;
    }

    @Override
    public IndexResponse indexDoc(Document doc) throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.addDocument(doc);

        int numIndexed = indexWriter.getDocStats().maxDoc;
        indexWriter.commit();
        indexWriter.close();

        return new IndexResponse(numIndexed);
    }

    @Override
    public void deleteDoc(String name) throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteDocuments(new Term("name", name));

        indexWriter.commit();
        indexWriter.close();
    }

    @Override
    public SearchResponse searchDoc(String queryString) throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        List<DocumentResponse> documents = new ArrayList<>();

        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
        Query query = queryBuilder.createBooleanQuery("contents", queryString);

        TopDocs topDocs = indexSearcher.search(query, 40);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String filename = doc.get("name");

            Documents document = documentsRepository.findByNameAndStatus(filename, EDocStatus.STATUS_STORED)
                    .orElse(null);

            documents.add(new DocumentResponse(document, scoreDoc.score));
        }
        return new SearchResponse(documents);
    }
}
