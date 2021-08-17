package org.usth.ict.ulake.folder.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.folder.model.UserFolder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FolderRepository implements PanacheRepository<UserFolder> {
}
