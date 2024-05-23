package org.usth.ict.ulake.textr.controllers;

import org.usth.ict.ulake.textr.models.ScheduledDocuments;
import org.usth.ict.ulake.textr.models.payloads.responses.MessageResponse;
import org.usth.ict.ulake.textr.services.ScheduledDocumentsService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/file/scheduled")
public class ScheduledDocumentsController {

    @Inject
    ScheduledDocumentsService scheduledDocumentsService;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScheduledDocuments() {
        List<ScheduledDocuments> scheduledDocuments;
        try {
            scheduledDocuments = scheduledDocumentsService.listAll();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse(404, e.getMessage())).build();
        }
        return Response.ok(scheduledDocuments).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScheduledDocumentById(@PathParam("id") Long id) {
        ScheduledDocuments scheduledDocument;
        try {
            scheduledDocument = scheduledDocumentsService.findByDocId(id);
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse(404, e.getMessage())).build();
        }
        return Response.ok(scheduledDocument).build();
    }
}
