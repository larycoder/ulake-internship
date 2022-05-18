package org.usth.ict.ulake.folder.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.folder.model.UserFolder;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FolderRepository implements PanacheRepository<UserFolder> {
    public List<UserFolder> listRoot() {
        return list("parent = NULL");
    }
}
