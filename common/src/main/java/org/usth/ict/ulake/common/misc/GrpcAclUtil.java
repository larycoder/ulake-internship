package org.usth.ict.ulake.common.misc;

import io.quarkus.example.AclGrpcModel;
import io.quarkus.example.FileType;
import io.quarkus.example.PermissionModel;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.service.AclService;

@ApplicationScoped
public class GrpcAclUtil {
    private static final Logger log = LoggerFactory.getLogger(GrpcAclUtil.class);

    @Inject
    @GrpcClient
    io.quarkus.example.AclGrpcServiceGrpc.AclGrpcServiceBlockingStub aclGrpcService;

    @Inject
    JsonWebToken jwt;

    public GrpcAclUtil() {}

    public Boolean verify(FileType type, Long objId,
                          Long owner, PermissionModel permit) {
        return verify(type, objId, owner, permit, true);
    }

    public Boolean verify(io.quarkus.example.FileType type, Long objId, Long owner,
                          io.quarkus.example.PermissionModel permit, Boolean checkShare) {
//         admin and owner pre-check
        if (jwt.getGroups().contains("Admin")){
            return true;
        }
        else if (owner.equals(Long.parseLong(jwt.getClaim(Claims.sub)))){
            return true;
        }

        if (checkShare != null && checkShare.equals(false)) // do not check share
        {
            return false;
        }

//         shared check
        AclGrpcModel acl = AclGrpcModel.newBuilder()
                .setObjectId(objId)
                .setUserId(Long.parseLong(jwt.getClaim(Claims.sub)))
                .setPermission(permit).build();

        try {
            String bearer = "bearer " + jwt.getRawToken();
            var ok = aclGrpcService.validate(io.quarkus.example.ValidateRequest.newBuilder()
                    .setBearer(bearer)
                    .setFileType(type)
                    .setFile(acl)
                    .build()).getToken();
            return ok.equals("true");
        } catch (Exception e) {
            log.error("Acl validation fail", e);
            return false;
        }
//        return true;
    }
}
