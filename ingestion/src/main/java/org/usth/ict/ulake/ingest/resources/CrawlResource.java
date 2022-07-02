package org.usth.ict.ulake.ingest.resources;

import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.services.CrawlSvc;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/api/v1/crawl")
public class CrawlResource {
    @Inject
    CrawlSvc svc;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public DataModel runCrawl(
            @QueryParam("mode") String mode, Map policy) {
        return svc.runCrawl(policy, mode);
    }
}
