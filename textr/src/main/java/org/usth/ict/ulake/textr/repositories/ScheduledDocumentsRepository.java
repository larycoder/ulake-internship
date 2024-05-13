package org.usth.ict.ulake.textr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.usth.ict.ulake.textr.models.ScheduledDocuments;

import java.util.Optional;

@Repository
public interface ScheduledDocumentsRepository extends JpaRepository<ScheduledDocuments, Long> {
    void deleteByDocId(Long docId);

    Optional<ScheduledDocuments> findByDocId(Long docId);
}
