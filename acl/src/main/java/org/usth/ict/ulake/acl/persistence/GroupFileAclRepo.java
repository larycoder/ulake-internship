package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.FileAcl;
import org.usth.ict.ulake.acl.model.GroupFileAcl;
import org.usth.ict.ulake.common.misc.Utils;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GroupFileAclRepo implements PanacheRepository<GroupFileAcl> {
    @Inject
    EntityManager em;

    public List<GroupFileAcl> findAcl(GroupFileAcl acl) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("(groupId = :groupId)");
        params.put("groupId", acl.groupId);

        conditions.add("(fileId = :fileId)");
        params.put("fileId", acl.fileId);

        conditions.add("(permission = :permission)");
        params.put("permission", acl.permission);

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<GroupFileAcl> listAcl() {
        String hql = "FROM GroupFileAcl GROUP BY groupId, fileId";
        var result = em.createQuery(hql, GroupFileAcl.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public Boolean hasAcl(GroupFileAcl acl) {
        return !findAcl(acl).isEmpty();
    }

    public List<GroupFileAcl> listAcl(FileAcl file) {
        List<String> query = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();

        if (file.fileId != null) {
            query.add("(fileId = :fileId)");
            params.put("fileId", file.fileId);
        }

        if (file.groupIds != null) {
            query.add("(groupId in (:groupIds))");
            params.put("groupIds", file.groupIds);
        }

        if (file.permission != null) {
            query.add("(permission = :permission)");
            params.put("permission", file.permission);
        }

        String hql = String.join(" and ", query);
        System.out.println(hql);
        return list(hql, params);
    }
}
