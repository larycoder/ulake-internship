package org.usth.ict.ulake.folder.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserSearchQuery;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FileRepository implements PanacheRepository<UserFile> {
    public List<UserFile> search(UserSearchQuery query) {
        return null;
    }
}
