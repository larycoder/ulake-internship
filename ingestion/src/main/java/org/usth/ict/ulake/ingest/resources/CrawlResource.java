package org.usth.ict.ulake.ingest.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.services.CrawlSvc;

@Path("/crawl")
public class CrawlResource {
    @Inject
    CrawlSvc svc;

    @Inject
    ObjectMapper mapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public DataModel runCrawl(
        @QueryParam("mode") String mode,
        @RequestBody Policy policy) {
        String policyMap = null;
        try {
            policyMap = mapper.writeValueAsString(policy);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return svc.runCrawl(policyMap, mode);
    }
}
