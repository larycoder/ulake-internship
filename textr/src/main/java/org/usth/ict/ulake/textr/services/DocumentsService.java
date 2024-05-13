package org.usth.ict.ulake.textr.services;

import org.apache.lucene.document.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.payloads.requests.MultipartBody;
import org.usth.ict.ulake.textr.models.ScheduledDocuments;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.MessageResponse;
import org.usth.ict.ulake.textr.repositories.DocumentsRepository;
import org.usth.ict.ulake.textr.repositories.ScheduledDocumentsRepository;
import org.usth.ict.ulake.textr.services.engines.IndexSearchEngineV2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DocumentsService {

    @ConfigProperty(name = "textr.documents.dataDir")
    String dataDir;

    @Inject
    IndexSearchEngineV2 indexSearchEngine;

    @Autowired
    DocumentsRepository documentsRepository;

    @Autowired
    ScheduledDocumentsRepository scheduledDocumentsRepository;

    public MessageResponse upload(MultipartBody multipartBody) {
        if (documentsRepository.existsByNameAndStatus(multipartBody.getFilename(), EDocStatus.STATUS_STORED))
            return new MessageResponse(400, "File already uploaded");

        IndexResponse indexResponse;

        try {
            BufferedReader contents = new BufferedReader(new InputStreamReader(multipartBody.getFile()));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = contents.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            String fileContents = contentBuilder.toString();

            Document doc = indexSearchEngine.getDocument(multipartBody.getFilename(), fileContents);
            indexResponse = indexSearchEngine.indexDoc(doc);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get(dataDir + multipartBody.getFilename()))) {
                writer.write(fileContents);
            }

            Documents documents = new Documents(multipartBody.getFilename(), EDocStatus.STATUS_STORED);
            documentsRepository.save(documents);
        } catch (Exception e) {
            return new MessageResponse(500, "File upload failed" + e.getMessage());
        }
        return new MessageResponse(200, indexResponse.getIndexed() + " new files indexed in database");
    }

    public List<Documents> listAllByStatus(EDocStatus status) throws RuntimeException {
        List<Documents> documents = documentsRepository.findAllByStatus(status);

        if (documents.isEmpty())
            throw new RuntimeException("No documents found");

        return documents;
    }

    public MessageResponse updateStatusById(Long id, EDocStatus status) {
        Documents doc = documentsRepository.findById(id).orElse(null);

        if (doc == null)
            return new MessageResponse(404, "No document found");

        if(doc.getStatus() == status)
            return new MessageResponse(400, "Document status already set");

        if (status == EDocStatus.STATUS_DELETED) {
            try {
                ScheduledDocuments scheduledDocuments = new ScheduledDocuments(doc);
                scheduledDocumentsRepository.save(scheduledDocuments);

                indexSearchEngine.deleteDoc(doc.getName());
            } catch (IOException e) {
                return new MessageResponse(500, "Delete index failed: " + e.getMessage());
            }
        } else {
            try {
                scheduledDocumentsRepository.deleteByDocId(doc.getId());

                File file = new File(dataDir + doc.getName());
                Document restoredDoc = indexSearchEngine.getDocument(file.getName(), file);
                indexSearchEngine.indexDoc(restoredDoc);
            } catch (Exception e) {
                return new MessageResponse(500, "File restored failed: " + e.getMessage());
            }
        }
        documentsRepository.updateStatusByDocument(doc, status);

        return new MessageResponse(200, "Document status updated");
    }
}
