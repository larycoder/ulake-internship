package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.GroupFolderAcl;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GroupFolderAclRepo implements PanacheRepository<GroupFolderAcl> {
    @Inject
    EntityManager em;

    public List<GroupFolderAcl> findAcl(GroupFolderAcl acl) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("(groupId = :groupId)");
        params.put("groupId", acl.groupId);

        conditions.add("(folderId = :folderId)");
        params.put("folderId", acl.folderId);

        conditions.add("(permission = :permission)");
        params.put("permission", acl.permission);

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<GroupFolderAcl> listAcl() {
        String hql = "FROM GroupFolderAcl GROUP BY groupId, folderId";
        var result = em.createQuery(hql, GroupFolderAcl.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public Boolean hasAcl(GroupFolderAcl acl) {
        return !findAcl(acl).isEmpty();
    }
}
