package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.FileAcl;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FileAclRepo implements PanacheRepository<FileAcl> {
    @Inject
    EntityManager em;

    public List<FileAcl> findAcl(FileAcl acl) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("(userId = :userId)");
        params.put("userId", acl.userId);

        conditions.add("(fileId = :fileId)");
        params.put("fileId", acl.fileId);

        conditions.add("(permission = :permission)");
        params.put("permission", acl.permission);

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<FileAcl> listAcl() {
        String hql = "FROM FileAcl GROUP BY userId, fileId";
        var result = em.createQuery(hql, FileAcl.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public Boolean hasAcl(FileAcl acl) {
        return !findAcl(acl).isEmpty();
    }
}
