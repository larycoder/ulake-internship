package org.usth.ict.ulake.textr.controllers;

import org.usth.ict.ulake.textr.models.payloads.responses.MessageResponse;
import org.usth.ict.ulake.textr.models.payloads.responses.SearchResponse;
import org.usth.ict.ulake.textr.services.SearchService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/search")
public class SearchController {

    @Inject
    SearchService searchService;

    @GET
    @Path("/{term}")
    public Response search(@PathParam("term") String term) {
        try {
            SearchResponse searchResponse = searchService.search(term);
            return Response.ok(searchResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse(500, e.getMessage())).build();
        }
    }
}
