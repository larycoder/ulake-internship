package org.usth.ict.ulake.textr.services.engines;

import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Paths;

@Singleton
public class LuceneManager {

    @Getter
    private final Analyzer analyzer = new StandardAnalyzer();

    @Singleton
    private IndexWriter indexWriter;

    private SearcherManager searcherManager;

    private ControlledRealTimeReopenThread<IndexSearcher> nrtReopenThread;

    @ConfigProperty(name = "textr.documents.indexDir")
    String indexPath;

    @PostConstruct
    protected void init() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(new BM25Similarity());
        indexWriter = new IndexWriter(dir, config);

        searcherManager = new SearcherManager(indexWriter, true, true, null);

        nrtReopenThread = new ControlledRealTimeReopenThread<>(indexWriter, searcherManager, 5.0, 0.025);
        nrtReopenThread.start();
    }

    private void ensureWritable() throws IOException {
        if (!indexWriter.isOpen()) {
            searcherManager.close();
            nrtReopenThread.close();
            this.init();
        }
    }

    public IndexWriter getIndexWriter() throws IOException {
        this.ensureWritable();
        return indexWriter;
    }

    public void maybeMerge() throws IOException {
        this.ensureWritable();
        indexWriter.maybeMerge();
    }

    public void forceMerge() throws IOException {
        this.ensureWritable();
        indexWriter.forceMergeDeletes();
    }

    public IndexSearcher getIndexSearcher() throws IOException {
        return searcherManager.acquire();
    }

    public void releaseIndexSearcher(IndexSearcher indexSearcher) throws IOException {
        searcherManager.release(indexSearcher);
    }

    public void commit() throws IOException {
        this.ensureWritable();
        indexWriter.commit();
        searcherManager.maybeRefresh();
    }

    public void close() throws IOException {
        nrtReopenThread.close();
        searcherManager.close();
        indexWriter.close();
    }
}
