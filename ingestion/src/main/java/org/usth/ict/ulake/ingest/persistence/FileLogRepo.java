package org.usth.ict.ulake.ingest.persistence;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.model.FileLog;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FileLogRepo implements PanacheRepository<FileLog> {
    public List<FileLog> findByProcessId(Long id) {
        return find("process_id = ?1", id).list();
    }
}
