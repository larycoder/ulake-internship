package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.UserFolderAcl;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserFolderAclRepo implements PanacheRepository<UserFolderAcl> {
    @Inject
    EntityManager em;

    public List<UserFolderAcl> findAcl(UserFolderAcl acl) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("(userId = :userId)");
        params.put("userId", acl.userId);

        conditions.add("(folderId = :folderId)");
        params.put("folderId", acl.folderId);

        conditions.add("(permission = :permission)");
        params.put("permission", acl.permission);

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<UserFolderAcl> listAcl() {
        String hql = "FROM UserFolderAcl GROUP BY userId, folderId";
        var result = em.createQuery(hql, UserFolderAcl.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public Boolean hasAcl(UserFolderAcl acl) {
        return !findAcl(acl).isEmpty();
    }
}
