package org.usth.ict.ulake.textr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.ScheduledDocuments;
import org.usth.ict.ulake.textr.repositories.ScheduledDocumentsRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ScheduledDocumentsService {

    @Autowired
    ScheduledDocumentsRepository scheduledDocumentsRepository;

    public List<ScheduledDocuments> listAll() throws RuntimeException {
        List<ScheduledDocuments> scheduledDocuments = scheduledDocumentsRepository.findAll();

        if (scheduledDocuments.isEmpty()) {
            throw new RuntimeException("No scheduled documents found");
        }
        return scheduledDocuments;
    }

    public ScheduledDocuments findByDocId(Long docId) {
        return scheduledDocumentsRepository.findByDocId(docId)
                .orElseThrow(() -> new RuntimeException("No scheduled documents found"));
    }
}
