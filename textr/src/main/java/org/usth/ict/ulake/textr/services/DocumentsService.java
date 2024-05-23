package org.usth.ict.ulake.textr.services;

import org.apache.lucene.document.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.ScheduledDocuments;
import org.usth.ict.ulake.textr.models.payloads.requests.MultipartBody;
import org.usth.ict.ulake.textr.models.payloads.responses.FileResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.IndexResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.MessageResponse;
import org.usth.ict.ulake.textr.repositories.DocumentsRepository;
import org.usth.ict.ulake.textr.repositories.ScheduledDocumentsRepository;
import org.usth.ict.ulake.textr.services.engines.IndexSearchEngineV2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DocumentsService {

    @ConfigProperty(name = "textr.documents.dataDir")
    String dataDir;

    @ConfigProperty(name = "textr.scheduled.permanentDelDays")
    int permanentDelDays;

    @Inject
    IndexSearchEngineV2 indexSearchEngine;

    @Autowired
    DocumentsRepository documentsRepository;

    @Autowired
    ScheduledDocumentsRepository scheduledDocumentsRepository;

    private String getNewName(String filename) {
        String docName = filename.split("\\.")[0];
        String mime = "." + filename.split("\\.")[1];
        List<String> filenames = documentsRepository.findNamesByDocName(docName);

        List<String> fileIndices = new ArrayList<>();
        for (String fName : filenames) {
            fName = fName.replaceAll(docName, "")
                    .replaceAll(mime, "")
                    .replaceAll("[()]", "");
            fileIndices.add(fName);
        }
        long idx = 1;
        while (isInIndices(idx, fileIndices))
            idx++;

        return docName + "(" + idx + ")" + mime;
    }

    private boolean isInIndices(long idx, List<String> indices) {
        for (String index : indices) {
            if (Objects.equals(index, String.valueOf(idx)))
                return true;
        }
        return false;
    }

    public MessageResponse upload(MultipartBody multipartBody) {
        String filename = multipartBody.getFilename();
        if (documentsRepository.existsByName(filename)) {
            filename = getNewName(filename);
        }

        IndexResponse indexResponse;
        Documents documents = null;

        File file = new File(dataDir + filename);
        try {
            InputStream inputStream = multipartBody.getFile();
            OutputStream outputStream = new FileOutputStream(file, false);
            inputStream.transferTo(outputStream);
            inputStream.close();

            documents = new Documents(filename, dataDir, EDocStatus.STATUS_STORED);
            documentsRepository.save(documents);

            Document doc = indexSearchEngine.getDocument(documents.getId(), filename, file);
            indexResponse = indexSearchEngine.indexDoc(doc);
            indexSearchEngine.commit();
        } catch (Exception e) {
            if (file.delete())
                documentsRepository.delete(documents);
            return new MessageResponse(500, "File upload failed: " + e.getMessage()
                    + ". Rolled-back");
        }
        return new MessageResponse(200, indexResponse.getIndexed() + " files uploaded and indexed in database");
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

        if (doc.getStatus() == status)
            return new MessageResponse(400, "Document status already set: " + status.name());

        if (status == EDocStatus.STATUS_DELETED) {
            try {
                setDeleted(doc);
            } catch (Exception e) {
                return new MessageResponse(500, "File deletion failed: " + e.getMessage());
            }
        } else {
            try {
                setRestored(doc);
            } catch (Exception e) {
                return new MessageResponse(500, "File restoration failed: " + e.getMessage());
            }
        }
        documentsRepository.updateStatusByDocument(doc, status);

        return new MessageResponse(200, "Document status updated: " + status.name());
    }

    private void setDeleted(Documents doc) throws RuntimeException {
        String docName = doc.getName();

        File file = new File(dataDir + docName);
        File targetFile = new File(dataDir + "deleted/" + docName);

        if (!file.renameTo(targetFile))
            throw new RuntimeException("Unable to move file");

        ScheduledDocuments scheduledDocuments = new ScheduledDocuments(doc, permanentDelDays);
        scheduledDocumentsRepository.save(scheduledDocuments);
    }

    private void setRestored(Documents doc) throws RuntimeException {
        String docName = doc.getName();

        File file = new File(dataDir + "deleted/" + docName);
        File targetFile = new File(dataDir + docName);

        if (!file.renameTo(targetFile))
            throw new RuntimeException("Unable to move file");

        scheduledDocumentsRepository.deleteByDocId(doc.getId());
    }

    public FileResponse getFileById(Long id) {
        Documents doc = documentsRepository.findById(id).orElse(null);

        if (doc == null)
            return null;

        String docName = doc.getName();
        String encodedFilename;

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(dataDir + docName);

            encodedFilename = URLEncoder.encode(docName, StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            return null;
        }
        return new FileResponse(encodedFilename, inputStream::transferTo);
    }
}
