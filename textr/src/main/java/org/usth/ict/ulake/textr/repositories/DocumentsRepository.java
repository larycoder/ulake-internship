package org.usth.ict.ulake.textr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Long> {

    boolean existsByNameAndStatus(String name, EDocStatus status);

    Optional<Documents> findByIdAndStatus(Long id, EDocStatus status);

    List<Documents> findAllByStatus(EDocStatus status);

    Optional<Documents> findById(Long id);

    @Modifying
    @Query("update Documents d set d.status = :status where d = :doc")
    void updateStatusByDocument(Documents doc, EDocStatus status);
}

