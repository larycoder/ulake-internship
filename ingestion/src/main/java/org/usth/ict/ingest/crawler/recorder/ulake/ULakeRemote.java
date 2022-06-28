package org.usth.ict.ingest.crawler.recorder.ulake;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ingest.models.ulake.StorageObjectModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RegisterRestClient
@RegisterForReflection
public interface ULakeRemote {
    @POST
    @Path("/api/object")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response uploadStorageObject(
            @HeaderParam("Authorization") String token,
            @MultipartForm StorageObjectModel file);
}
