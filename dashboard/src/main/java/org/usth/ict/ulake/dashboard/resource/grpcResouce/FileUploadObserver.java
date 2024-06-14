package org.usth.ict.ulake.dashboard.resource.grpcResouce;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.UploadCoreRequest;
import io.quarkus.example.UploadCoreResponse;

class FileUploadObserver implements StreamObserver<UploadCoreResponse> {

    @Override
    public void onNext(UploadCoreResponse fileUploadResponse) {
        System.out.println(
                "File upload status :: " + fileUploadResponse.getCode()
        );
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }

}
