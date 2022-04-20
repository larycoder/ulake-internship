package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.acl.model.AclModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AclRepository implements PanacheRepository<AclModel> {
    public Boolean hasAcl(AclModel acl) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("(userId = :userId)");
        params.put("userId", acl.getUserId());

        conditions.add("(objectId = :objectId)");
        params.put("objectId", acl.getObjectId());

        conditions.add("(permission = :permission)");
        params.put("permission", acl.getPermission());

        String hql = String.join(" and ", conditions);
        if(list(hql, params).isEmpty())
            return false;
        else
            return true;
    }
}
