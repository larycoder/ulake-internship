package org.usth.ict.ulake.textr.controllers;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.textr.models.Documents;
import org.usth.ict.ulake.textr.models.EDocStatus;
import org.usth.ict.ulake.textr.models.payloads.requests.MultipartBody;
import org.usth.ict.ulake.textr.models.payloads.responses.FileResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.MessageResponse;
import org.usth.ict.ulake.textr.services.DocumentsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/file")
public class DocumentsController {

    @Inject
    DocumentsService documentsService;

    @POST
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@MultipartForm MultipartBody body) {
        MessageResponse messageResponse = documentsService.upload(body);

        return Response.status(messageResponse.getStatus()).entity(messageResponse).build();
    }

    @GET
    @Path("/list/stored")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllStored() {
        List<Documents> documents;
        try {
            documents = documentsService.listAllByStatus(EDocStatus.STATUS_STORED);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse(404, e.getMessage()))
                    .build();
        }
        return Response.ok(documents).build();
    }

    @GET
    @Path("/list/deleted")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllDeleted() {
        List<Documents> documents;
        try {
            documents = documentsService.listAllByStatus(EDocStatus.STATUS_DELETED);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse(404, e.getMessage()))
                    .build();
        }
        return Response.ok(documents).build();
    }

    @POST
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        MessageResponse messageResponse = documentsService.updateStatusById(id, EDocStatus.STATUS_DELETED);

        return Response.status(messageResponse.getStatus()).entity(messageResponse).build();
    }

    @POST
    @Path("/restore/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response restore(@PathParam("id") Long id) {
        MessageResponse messageResponse = documentsService.updateStatusById(id, EDocStatus.STATUS_STORED);

        return Response.status(messageResponse.getStatus()).entity(messageResponse).build();
    }

    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("id") Long id) {
        FileResponse fileResponse = documentsService.getFileById(id);

        if (fileResponse == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse(404, "File not found")).build();

        return Response.ok(fileResponse.getFileStream())
                .header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                .build();
    }
}
