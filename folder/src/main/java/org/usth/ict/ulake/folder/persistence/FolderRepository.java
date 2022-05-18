package org.usth.ict.ulake.folder.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.folder.model.UserFolder;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FolderRepository implements PanacheRepository<UserFolder> {
    public List<UserFolder> load(List<UserFolder> detach) {
        UserFolder attach;

        if (detach == null || detach.isEmpty())
            return null;

        var result = new ArrayList<UserFolder>();
        for (var file : detach) {
            if (file.id != null) {
                attach = findById(file.id);
                if (attach != null) result.add(attach);
            }
        }

        return result;
    }

    public List<UserFolder> listRoot() {
        return list("parent = NULL");
    }
}
