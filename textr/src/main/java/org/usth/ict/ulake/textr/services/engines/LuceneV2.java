package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@ApplicationScoped
public class LuceneV2 implements IndexSearchEngineV2{

    @ConfigProperty(name = "textr.documents.indexDir")
    String indexDir;

    private final Analyzer analyzer = new StandardAnalyzer();

    private IndexWriter indexWriter;

    @PostConstruct
    protected void initIndexWriter() throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriterConfig.setSimilarity(new BM25Similarity());

        assert indexDir != null;
        Directory indexDirectory = FSDirectory.open(new File(indexDir).toPath());

        this.indexWriter =  new IndexWriter(indexDirectory, indexWriterConfig);
    }

    @PreDestroy
    protected void closeIndexWriter() throws IOException {
        this.indexWriter.close();
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
        indexWriter.addDocument(doc);

        int numIndexed = indexWriter.getDocStats().maxDoc;
        indexWriter.commit();

        return new IndexResponse(numIndexed);
    }

    public void deleteDoc(String name) throws IOException {
        indexWriter.deleteDocuments(new Term("name", name));

        indexWriter.commit();
    }
}
