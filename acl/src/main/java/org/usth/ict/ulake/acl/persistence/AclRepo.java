package org.usth.ict.ulake.acl.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.model.acl.MultiAcl;
import org.usth.ict.ulake.common.model.acl.macro.AclType;
import org.usth.ict.ulake.common.model.acl.macro.FileType;
import org.usth.ict.ulake.common.model.acl.macro.UserType;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AclRepo implements PanacheRepository<AclModel> {
    @Inject
    EntityManager em;

    public List<AclModel> findAcl(FileType type, Acl acl) {
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // filter by user and list of user group
        List<String> sub = new ArrayList<>();
        if (acl.userId != null) {
            sub.add("(userId = :uUserId AND type = :uType)");
            params.put("uUserId", acl.userId);
            params.put("uType", AclType.valueOf("u" + type.label));
        }
        if (!Utils.isEmpty(acl.groupIds)) {
            sub.add("(userId in :UUserId AND type = :gType)");
            params.put("gUserId", acl.groupIds);
            params.put("gType", AclType.valueOf("g" + type.label));
        }
        if (!sub.isEmpty())
            conditions.add("(" + String.join(" OR ", sub) + ")");

        conditions.add("(objectId = :objectId)");
        params.put("objectId", acl.objectId);

        conditions.add("(permission = :permission)");
        params.put("permission", acl.permission);

        String hql = String.join(" AND ", conditions);
        return list(hql, params);
    }

    public List<AclModel> listAcl() {
        String hql = "FROM AclModel GROUP BY type, userId, objectId";
        var result = em.createQuery(hql, AclModel.class).getResultList();
        for (var obj : result) obj.permission = null;
        return result;
    }

    public List<MultiAcl> listActorMultiAcl(UserType actor, Long actorId) {
        AclType folderType = AclType.valueOf(actor.label + "d");
        AclType fileType = AclType.valueOf(actor.label + "f");
        String hql = "(type = ?1 or type= ?2) and (userId = ?3)";
        List<AclModel> acls = list(hql, folderType, fileType, actorId);

        // TODO: add function for merging permissions for better code reuse
        Map<Long, MultiAcl> aclByObject = new HashMap<>();
        for (var acl : acls) {
            if (aclByObject.get(acl.objectId) == null) {
                var multi = new MultiAcl();
                multi.objectId = acl.objectId;
                multi.userId = acl.userId;
                if (acl.type == AclType.gd || acl.type == AclType.ud) multi.type = FileType.folder;
                if (acl.type == AclType.gf || acl.type == AclType.uf) multi.type = FileType.file;
                multi.permissions = new ArrayList<>();
                aclByObject.put(acl.objectId, multi);
            }
            aclByObject.get(acl.objectId).permissions.add(acl.permission);
        }
        return new ArrayList<MultiAcl>(aclByObject.values());
    }

    public List<MultiAcl> listMultiAcl(AclType type, Long objectId) {
        String hql = "(objectId = ?1) AND (type = ?2)";
        List<AclModel> acls = find(hql, objectId, type).list();
        Map<Long, MultiAcl> myAcls = new HashMap<>();

        for (var acl : acls) {
            if (myAcls.get(acl.userId) == null) {
                var multi = new MultiAcl();
                multi.objectId = acl.objectId;
                multi.userId = acl.userId;
                if (acl.type == AclType.gd || acl.type == AclType.ud) multi.type = FileType.folder;
                if (acl.type == AclType.gf || acl.type == AclType.uf) multi.type = FileType.file;
                multi.permissions = new ArrayList<>();
                myAcls.put(acl.userId, multi);
            }
            myAcls.get(acl.userId).permissions.add(acl.permission);
        }
        return new ArrayList<MultiAcl>(myAcls.values());
    }


    public MultiAcl sync(AclType type, Long fileOwner, MultiAcl acl) {
        String hql = "type = ?1 and objectId = ?2 and userId = ?3";

        String delHql = hql + " and permission not in ?4";
        if (Utils.isEmpty(acl.permissions))
            this.delete(hql, type, acl.objectId, acl.userId);
        else
            this.delete(delHql, type, acl.objectId, acl.userId, acl.permissions);

        List<PermissionModel> permits = find(hql, type, acl.objectId, acl.userId)
                                        .list().stream().map((a) -> a.permission)
                                        .collect(Collectors.toList());

        for (var permit : acl.permissions) {
            if (!permits.contains(permit)) {
                var myAcl = new AclModel();
                myAcl.type = type;
                myAcl.ownerId = fileOwner;
                myAcl.objectId = acl.objectId;
                myAcl.userId = acl.userId;
                myAcl.permission = permit;
                persist(myAcl);
            }
        }

        acl.permissions = find(hql, type, acl.objectId, acl.userId)
                          .list().stream().map((a) -> a.permission)
                          .collect(Collectors.toList());
        return acl;
    }
}
