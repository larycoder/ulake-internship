package org.usth.ict.ulake.table.resource.grpc;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.LogGrpcServiceGrpc;
import io.quarkus.example.LogRequest;
import io.quarkus.example.LogResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.usth.ict.ulake.table.persistence.LogRepository;

import java.util.List;

@GrpcService
@Blocking
public class grpcLogResource extends LogGrpcServiceGrpc.LogGrpcServiceImplBase {
    @Inject
    LogRepository repo;

    @Inject
    JsonWebToken jwt;

    @Override
    public void getLog(LogRequest request, StreamObserver<LogResponse> responseObserver) {
        List<org.usth.ict.ulake.table.model.LogEntry> logs = repo.listAll();
        LogResponse.Builder response = LogResponse.newBuilder();
        for (org.usth.ict.ulake.table.model.LogEntry log : logs) {
            response.setId(log.id);
            response.setContent(log.content);
            response.setOwnerId(log.ownerId);
            response.setTimestamp(log.timestamp);
            response.setTimestamp(log.timestamp);
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

////take the first log entry
//        var log = logs.get(0);
//        responseObserver.onNext(LogResponse.newBuilder().setId(log.id).setContent(log.content).setOwnerId(log.ownerId).setTimestamp(log.timestamp).build());
//        responseObserver.onCompleted();

//        responseObserver.onNext(LogResponse.newBuilder().setId(1).setContent("content").setOwnerId(1).setTimestamp(1).build());
//        responseObserver.onCompleted();
    }
}
