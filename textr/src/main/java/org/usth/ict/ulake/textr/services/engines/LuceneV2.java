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
import org.usth.ict.ulake.textr.models.Documents;
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
    public Document getDocument(String name, File file) throws IOException, TikaException {
        String contents = tikaExtractor.extractText(file);

        return getDocument(name, contents);
    }

    @Override
    public Document getDocument(String name, String contents) {
        Document doc = new Document();

        FieldType contentFieldType = new FieldType();
        contentFieldType.setStored(true);
        contentFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        contentFieldType.setStoreTermVectors(true);
        contentFieldType.setStoreTermVectorPositions(true);
        contentFieldType.setStoreTermVectorOffsets(true);
        contentFieldType.freeze();

        FieldType nameFieldType = new FieldType();
        nameFieldType.setStored(true);
        nameFieldType.setIndexOptions(IndexOptions.DOCS);
        nameFieldType.setTokenized(false);
        nameFieldType.setOmitNorms(false);
        nameFieldType.freeze();

        doc.add(new Field(LuceneConstants.FILE_NAME, name, nameFieldType));
        doc.add(new Field(LuceneConstants.CONTENTS, contents, contentFieldType));

        return doc;
    }

    @Override
    public IndexResponse indexDoc(Document doc) throws IOException {
        IndexWriter indexWriter = luceneManager.getIndexWriter();
        indexWriter.addDocument(doc);

        int numIndexed = indexWriter.getDocStats().numDocs;
        luceneManager.commit();

        return new IndexResponse(numIndexed);
    }

    @Override
    public void deleteDoc(String name) throws IOException {
        IndexWriter indexWriter = luceneManager.getIndexWriter();

        indexWriter.deleteDocuments(new Term(LuceneConstants.FILE_NAME, name));

        luceneManager.commit();
    }

    @Override
    public SearchResponse searchDoc(String queryString) throws IOException, InvalidTokenOffsetsException {
        IndexSearcher indexSearcher = luceneManager.getIndexSearcher();

        List<DocumentResponse> documents = new ArrayList<>();

        try {
            Analyzer analyzer = luceneManager.getAnalyzer();

            QueryBuilder queryBuilder = new QueryBuilder(analyzer);
            Query query = queryBuilder.createBooleanQuery(LuceneConstants.CONTENTS, queryString);

            TopDocs hits = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
            StoredFields storedFields = indexSearcher.storedFields();
            for (ScoreDoc hit : hits.scoreDocs) {
                Document doc = storedFields.document(hit.doc);

                QueryScorer scorer = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(scorer);
                String[] highlightContents = highlighter
                        .getBestFragments(analyzer, LuceneConstants.CONTENTS, doc.get(LuceneConstants.CONTENTS), 2);

                String filename = doc.get(LuceneConstants.FILE_NAME);

                Documents document = documentsRepository.findByNameAndStatus(filename, EDocStatus.STATUS_STORED)
                        .orElse(null);

                documents.add(new DocumentResponse(document, hit.score, highlightContents));
            }
        } finally {
            luceneManager.releaseIndexSearcher(indexSearcher);
        }

        return new SearchResponse(documents);
    }
}
