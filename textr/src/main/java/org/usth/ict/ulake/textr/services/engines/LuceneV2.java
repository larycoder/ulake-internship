package org.usth.ict.ulake.textr.services.engines;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.util.QueryBuilder;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;
import org.usth.ict.ulake.textr.repositories.DocumentsRepository;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LuceneV2 implements IndexSearchEngineV2 {

    @Inject
    TikaExtractor tikaExtractor;

    @Inject
    LuceneManager luceneManager;

    @Autowired
    DocumentsRepository documentsRepository;

    @PreDestroy
    protected void preDestroy() throws IOException {
        luceneManager.close();
    }

    @Override
    public Document getDocument(Long id, String filename, File file) throws IOException, TikaException {
        String contents = tikaExtractor.extractText(file);

        return getDocument(id, filename, contents);
    }

    @Override
    public Document getDocument(Long id, String filename, String contents) {
        filename = filename.replace(".", " ");

        Document doc = new Document();

        FieldType contentFieldType = new FieldType();
        contentFieldType.setStored(true);
        contentFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        contentFieldType.setStoreTermVectors(true);
        contentFieldType.setStoreTermVectorPositions(true);
        contentFieldType.setStoreTermVectorOffsets(true);
        contentFieldType.freeze();

        FieldType idFieldType = new FieldType();
        idFieldType.setStored(true);
        idFieldType.setIndexOptions(IndexOptions.DOCS);
        idFieldType.setTokenized(false);
        idFieldType.setOmitNorms(false);
        idFieldType.freeze();

        FieldType filenameFieldType = new FieldType();
        filenameFieldType.setStored(true);
        filenameFieldType.setIndexOptions(IndexOptions.DOCS);
        filenameFieldType.setTokenized(true);
        filenameFieldType.freeze();

        doc.add(new Field(LuceneConstants.ID, String.valueOf(id), idFieldType));
        doc.add(new Field(LuceneConstants.NAME, filename, filenameFieldType));
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
    public void deleteDoc(Long id) throws IOException {
        IndexWriter indexWriter = luceneManager.getIndexWriter();

        indexWriter.deleteDocuments(new Term(LuceneConstants.ID, String.valueOf(id)));
    }

    @Override
    public void commit() throws IOException {
        luceneManager.commit();
    }

    @Override
    public SearchResponse searchDoc(String queryString) throws IOException, InvalidTokenOffsetsException {
        IndexSearcher indexSearcher = luceneManager.getIndexSearcher();

        List<DocumentResponse> documents = new ArrayList<>();

        try {
            Analyzer analyzer = luceneManager.getAnalyzer();

            QueryBuilder queryBuilder = new QueryBuilder(analyzer);
            Query queryContents = queryBuilder.createBooleanQuery(LuceneConstants.CONTENTS, queryString);
            Query queryName = queryBuilder.createBooleanQuery(LuceneConstants.NAME, queryString);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(queryContents, BooleanClause.Occur.SHOULD);
            builder.add(queryName, BooleanClause.Occur.SHOULD);
            BooleanQuery query = builder.build();

            TopDocs hits = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
            StoredFields storedFields = indexSearcher.storedFields();
            for (ScoreDoc hit : hits.scoreDocs) {
                Document doc = storedFields.document(hit.doc);

                QueryScorer scorer = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(scorer);
                String[] highlightContents = highlighter
                        .getBestFragments(analyzer, LuceneConstants.CONTENTS, doc.get(LuceneConstants.CONTENTS), 4);

                Long id = Long.valueOf(doc.get(LuceneConstants.ID));

                documentsRepository.findByIdAndStatus(id, EDocStatus.STATUS_STORED)
                        .ifPresent(document -> documents.add(new DocumentResponse(document, hit.score, highlightContents)));
            }
        } finally {
            luceneManager.releaseIndexSearcher(indexSearcher);
        }
        return new SearchResponse(documents);
    }
}
