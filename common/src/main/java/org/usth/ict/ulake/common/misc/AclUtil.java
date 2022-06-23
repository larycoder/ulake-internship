package org.usth.ict.ulake.common.misc;

import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.service.AclService;

public class AclUtil {
    private static final Logger log = LoggerFactory.getLogger(AclUtil.class);

    public static Boolean verifyACL(
        AclService aclSvc, JsonWebToken jwt,
        Long objId, Long ownerId,
        PermissionModel permit) {
        Set<String> groups = jwt.getGroups();
        Long jwtId = Long.parseLong(jwt.getName());

        if (groups.contains("Admin"))
            return true;
        else if (ownerId.equals(jwtId))
            return true;
        else
            return verifyShareObject(aclSvc, jwt, objId, permit, log);
    }

    public static Boolean verifyACL(
        AclService aclSvc, JsonWebToken jwt,
        Long objId, Long ownerId,
        PermissionModel permit, Logger log) {
        Set<String> groups = jwt.getGroups();
        Long jwtId = Long.parseLong(jwt.getName());

        if (groups.contains("Admin"))
            return true;
        else if (ownerId.equals(jwtId))
            return true;
        else
            return verifyShareObject(aclSvc, jwt, objId, permit, log);
    }

    public static Boolean verifyShareObject(
        AclService aclSvc, JsonWebToken jwt,
        Long objId, PermissionModel permit, Logger log) {

        Acl acl = new Acl();
        acl.objectId = objId;
        acl.ownerId = Long.parseLong(jwt.getName());
        acl.permission = permit;
        try {
            var resp = aclSvc.validateFile("bearer " + jwt.getRawToken(), acl);
            if (resp.getCode() == 200)
                return (Boolean)resp.getResp();
            else
                return false;
        } catch (Exception e) {
            log.error("Acl validation fail", e);
            return false;
        }
    }
}
