package org.usth.ict.ulake.folder.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FileRepository implements PanacheRepository<UserFile> {
    public List<UserFile> load(List<UserFile> detach) {
        UserFile attach;

        if (detach == null || detach.isEmpty())
            return null;

        var result = new ArrayList<UserFile>();
        for (var file : detach) {
            if (file.id != null) {
                attach = findById(file.id);
                if (attach != null) result.add(attach);
            }
        }

        return result;
    }

    public List<UserFile> search(UserFileSearchQuery query) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (query.ownerIds != null && !query.ownerIds.isEmpty()) {
            var ownerIds = new ArrayList<Long>();
            for (var ownerId : query.ownerIds) {
                if (ownerId >= 0)
                    ownerIds.add(ownerId);
            }
            conditions.add("(ownerId in (:ownerIds))");
            params.put("ownerIds", ownerIds);
        }

        if (!Utils.isEmpty(query.keyword)) {
            conditions.add("(name like :keyword)");
            params.put("keyword", "%" + query.keyword + "%");
        }

        if (query.minSize != null && query.minSize > 0) {
            conditions.add("(size > :minSize)");
            params.put("minSize", query.minSize);
        }

        if (query.maxSize != null && query.maxSize > 0) {
            conditions.add("(size > :maxSize)");
            params.put("maxSize", query.maxSize);
        }

        if (!Utils.isEmpty(query.mime)) {
            conditions.add("(mime like :mime)");
            params.put("mime", "%" + query.mime + "%");
        }
        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<UserFile> listRoot(Long ownerId) {
        return list("(parent = NULL) AND (ownerId = ?1)", ownerId);
    }

    public List<UserFile> listRoot() {
        return list("parent = NULL");
    }
}
