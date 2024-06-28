package org.usth.ict.ulake.table.resource.grpc;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.LogGrpcServiceGrpc;
import io.quarkus.example.LogRequest;
import io.quarkus.example.LogResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.table.model.LogEntry;
import org.usth.ict.ulake.table.persistence.LogRepository;
import org.usth.ict.ulake.table.resource.LogResource;

import java.util.Date;
import java.util.List;

@GrpcService
@Blocking
public class grpcLogResource extends LogGrpcServiceGrpc.LogGrpcServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(grpcLogResource.class);

    @Inject
    LogRepository repo;

    @Inject
    JsonWebToken jwt;

    @POST
    @Transactional
    @PermitAll
    //@RolesAllowed({ "Admin" })
    @Operation(summary = "Make a new log entry")
    @Consumes(MediaType.APPLICATION_JSON)
    public LakeHttpResponse post(@RequestBody(description = "Log entry to save") LogEntry entity) {
        if (entity == null) {
            return new LakeHttpResponse(400, "Bad Request", null);
        }

        if ("".equals(entity.service) || entity.service == null) {
            entity.service = "Log";
        }
        if ("".equals(entity.tag) || entity.tag == null) {
            entity.service = "Log";
        }
        if (jwt.getClaim(Claims.sub) != null) {
            entity.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        }
        else {
            entity.ownerId = 0L;
        }
        entity.timestamp = new Date().getTime();
        repo.persist(entity);
        log.info("Making a new Log Entry at {}: {}", entity.timestamp, entity.content);
        return new LakeHttpResponse(200, "OK", entity);
    }
}
