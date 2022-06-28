package org.usth.ict.ingest.resources;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.usth.ict.ingest.models.DataModel;
import org.usth.ict.ingest.services.NameSpaceSvc;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/api/v1/name-spaces")
public class NameSpaceResource {
   @Inject
   NameSpaceSvc svc;

   @GET
   @Path("list")
   public DataModel list(){
      return svc.listAll();
   }

   @GET
   @Path("ids/{recordId}/info")
   public DataModel info(@PathParam int recordId){
      return svc.info(recordId);
   }

   @POST
   @Path("new")
   @Consumes(MediaType.APPLICATION_JSON)
   public DataModel add(Map data) {
      return svc.addRecord((String) data.get("name"));
   }

   @DELETE
   @Path("ids/{recordId}")
   public DataModel kill(@PathParam long recordId){
      return svc.deleteById(recordId);
   }
}
