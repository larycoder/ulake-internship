package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.UserFileAcl;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserFileAclRepo implements PanacheRepository<UserFileAcl> {
    @Inject
    EntityManager em;

    public List<UserFileAcl> findAcl(UserFileAcl acl) {
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

    public List<UserFileAcl> listAcl() {
        String hql = "FROM UserFileAcl GROUP BY userId, fileId";
        var result = em.createQuery(hql, UserFileAcl.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public Boolean hasAcl(UserFileAcl acl) {
        return !findAcl(acl).isEmpty();
    }
}
