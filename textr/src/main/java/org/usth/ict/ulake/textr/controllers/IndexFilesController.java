package org.usth.ict.ulake.textr.controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.textr.models.IndexingStatus;
import org.usth.ict.ulake.textr.models.payloads.responses.DocumentResponse;
import org.usth.ict.ulake.textr.services.ServiceResponseBuilder;
import org.usth.ict.ulake.textr.services.IndexFilesService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/textr")
@RolesAllowed({"User", "Admin"})
@ApplicationScoped
public class IndexFilesController {
    @Inject
    IndexFilesService service;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "upload and schedule a file")
    public Response upload(@HeaderParam("Authorization") String bearer, @MultipartForm FileFormModel fileFormModel) {
        ServiceResponseBuilder<?> serviceResponseBuilder = service.upload(bearer, fileFormModel);
        
        return serviceResponseBuilder.build();
    }
    
    @GET
    @Path("/deleted")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get all deleted files of user")
    public Response deleted(@DefaultValue("0") @QueryParam("page") int page,
                            @DefaultValue("50") @QueryParam("size") int size) {
        ServiceResponseBuilder<List<FileModel>> serviceResponseBuilder = service.listAllByStatus(IndexingStatus.STATUS_IGNORED, page,
                                                                                                 size);
        
        return serviceResponseBuilder.build();
    }
    
    @GET
    @Path("/scheduled")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get all files that being scheduled for index of user")
    public Response scheduled(@DefaultValue("0") @QueryParam("page") int page,
                              @DefaultValue("50") @QueryParam("size") int size) {
        ServiceResponseBuilder<List<FileModel>> serviceResponseBuilder = service.listAllByStatus(IndexingStatus.STATUS_SCHEDULED, page, size);
        
        return serviceResponseBuilder.build();
    }
    
    @GET
    @Path("/indexed")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get all indexed files of user")
    public Response indexed(@DefaultValue("0") @QueryParam("page") int page,
                            @DefaultValue("50") @QueryParam("size") int size) {
        ServiceResponseBuilder<List<FileModel>> serviceResponseBuilder = service.listAllByStatus(IndexingStatus.STATUS_INDEXED, page, size);
        
        return serviceResponseBuilder.build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all indexed and scheduled files of user")
    public Response get(@DefaultValue("0") @QueryParam("page") int page,
                        @DefaultValue("50") @QueryParam("size") int size) {
        IndexingStatus[] statuses = {IndexingStatus.STATUS_INDEXED, IndexingStatus.STATUS_SCHEDULED};
        ServiceResponseBuilder<List<FileModel>> serviceResponseBuilder = service.listAllByStatuses(List.of(statuses), page, size);
        
        return serviceResponseBuilder.build();
    }
    
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "delete a file of user")
    public Response delete(@HeaderParam("Authorization") String bearer, @PathParam("id") Long id) {
        ServiceResponseBuilder<?> serviceResponseBuilder = service.delete(bearer, id);
        
        return serviceResponseBuilder.build();
    }
    
    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "restore a file of user")
    public Response restore(@PathParam("id") Long id) {
        ServiceResponseBuilder<?> serviceResponseBuilder = service.restore(id);
        
        return serviceResponseBuilder.build();
    }
    
    @POST
    @Path("/{id}/reindex")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "re-index a file of user")
    public Response reindex(@PathParam("id") Long id) {
        ServiceResponseBuilder<?> serviceResponseBuilder = service.reindex(id);
        
        return serviceResponseBuilder.build();
    }
    
    @GET
    @Path("/{term}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "search for files")
    public Response search(@PathParam("term") String term) {
        ServiceResponseBuilder<List<DocumentResponse>> serviceResponseBuilder = service.search(term);
        
        return serviceResponseBuilder.build();
    }
}
