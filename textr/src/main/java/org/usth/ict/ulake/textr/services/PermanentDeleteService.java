package org.usth.ict.ulake.textr.services;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.ScheduledDocuments;
import org.usth.ict.ulake.textr.repositories.DocumentsRepository;
import org.usth.ict.ulake.textr.repositories.ScheduledDocumentsRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.File;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class PermanentDeleteService {

    @ConfigProperty(name = "textr.scheduled.permanentDelDays")
    int permanentDelDays;

    @ConfigProperty(name = "textr.documents.dataDir")
    String dataDir;

    @Autowired
    ScheduledDocumentsRepository scheduledDocumentsRepository;

    @Autowired
    DocumentsRepository documentsRepository;

    @Scheduled(cron = "{textr.scheduled.time}")
    void permanentDelete() {
        List<ScheduledDocuments> scheduledDocuments = scheduledDocumentsRepository.findAll();

        if (scheduledDocuments.isEmpty())
            return;

        for (ScheduledDocuments sd : scheduledDocuments) {
            if (isPermanentDelete(sd)) {
                Documents doc = sd.getDoc();

                File file = new File(dataDir + doc.getName());

                if (doc.getStatus().equals(EDocStatus.STATUS_DELETED) && file.delete()) {
                    scheduledDocumentsRepository.delete(sd);
                    documentsRepository.delete(doc);
                }
            }
        }
    }

    private Boolean isPermanentDelete(ScheduledDocuments sd) {
        Date currentDate = new Date();
        Date deletedDate = sd.getDeletedDate();

        long daysDiff = (currentDate.getTime() - deletedDate.getTime());

        return daysDiff >= (long) permanentDelDays * 24 * 60 * 60 * 1000;
    }
}
