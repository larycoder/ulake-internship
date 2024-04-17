package org.usth.ict.ulake.textr.engine;

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

import javax.enterprise.context.Dependent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

// Format supported by Lucene
//- TXT;
//- HTML;
//- RTF;
//- DOCX (Office Open XML – the binary DOC format is not supported);
//- PDF.

@Dependent
public class Lucene extends RootEngine{
    private final Directory indexDirectory;
    private final File dataDirectory;

    private final Analyzer analyzer = new StandardAnalyzer();

//    Constructor
    public Lucene() throws IOException {
//        Setup path
        String dataDir = "/home/malenquillaa/tmp/data";
        String indexDir = "/home/malenquillaa/tmp/index";
        this.indexDirectory = FSDirectory.open(new File(indexDir).toPath());
        this.dataDirectory = new File(dataDir);
    }

    @Override
    public int index(RootEngine engine) throws IOException {
        return engine.index();
    }

    public int index() throws IOException {
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

        return numIndexed;
    }

    @Override
    public HashMap<String, Float> search(RootEngine engine, String term) throws IOException{
        return engine.search(term);
    }

    public HashMap<String, Float> search(String queryString) throws IOException {
        HashMap<String, Float> filesMap = new HashMap<String, Float>();

//        Setup searcher
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

//        Setup query
        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
        Query query = queryBuilder.createPhraseQuery("content", queryString);

//        Search process
        TopDocs topDocs = indexSearcher.search(query, 100);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String filename = doc.get("filename");
            LOG.info("Searching in: " + filename);

//            Parse into Hashmap
            filesMap.put(filename, scoreDoc.score);
        }

        return filesMap;
    }
}
