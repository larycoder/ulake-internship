package org.usth.ict.ulake.common.misc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.model.acl.macro.FileType;
import org.usth.ict.ulake.common.service.AclService;

@ApplicationScoped
public class AclUtil {
    private static final Logger log = LoggerFactory.getLogger(AclUtil.class);

    @Inject
    @RestClient
    AclService svc;

    @Inject
    JsonWebToken jwt;

    public AclUtil() {}

    public Boolean verify(FileType type, Long objId,
                          Long owner, PermissionModel permit) {
        return verify(type, objId, owner, permit, true);
    }

    public Boolean verify(FileType type, Long objId, Long owner,
                          PermissionModel permit, Boolean checkShare) {
        // admin and owner pre-check
        if (jwt.getGroups().contains("Admin"))
            return true;
        else if (owner.equals(Long.parseLong(jwt.getName())))
            return true;

        if (checkShare != null && checkShare.equals(false)) // do not check share
            return false;

        // shared check
        Acl acl = new Acl();
        acl.objectId = objId;
        acl.userId = Long.parseLong(jwt.getName());
        acl.permission = permit;

        try {
            String bearer = "bearer " + jwt.getRawToken();
            return svc.validate(bearer, type, acl).getResp();
        } catch (Exception e) {
            log.error("Acl validation fail", e);
            return false;
        }
    }
}
