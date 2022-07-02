package org.usth.ict.ulake.ingest.resources;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.services.CrawlInfoSvc;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/crawl-info")
public class CrawlInfoResource {
    @Inject
    CrawlInfoSvc svc;

    @GET
    @Path("list")
    public DataModel list(){return svc.listAll();}

    @GET
    @Path("ids/{recordId}")
    public DataModel info(@PathParam long recordId){
        return svc.infoById(recordId);
    }

    @GET
    @Path("namespaces/{namespace}/list")
    public DataModel infoByNameSpace(@PathParam String namespace){
        return svc.infoByNameSpace(namespace);
    }

    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    public DataModel add(Map data){
        long name_id = Long.parseLong(data.get("name_id").toString());
        String link = data.get("link").toString();
        Map extra = (Map) data.get("extra");
        Map headers = (Map) data.get("headers");
        return svc.add(name_id, link, extra, headers);
    }

    @DELETE
    @Path("ids/{recordId}")
    public DataModel kill(@PathParam long recordId){
        return svc.deleteById(recordId);
    }
}
