package org.usth.ict.ulake.ingest.resources;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.PolicyModel;
import org.usth.ict.ulake.ingest.services.CrawlSvc;

@Path("/crawl")
public class CrawlResource {
    @Inject
    CrawlSvc svc;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public DataModel runCrawl(
        @QueryParam("mode") String mode,
        @RequestBody PolicyModel testPolicy) {
        Map<String, Object> policy = null;
        return svc.runCrawl(policy, mode);
    }
}
