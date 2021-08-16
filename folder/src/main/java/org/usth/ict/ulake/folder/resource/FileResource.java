package org.usth.ict.ulake.folder.resource;

import io.quarkus.hibernate.orm.panache.Panache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/file")
public class FileResource extends Panache {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello RESTEasy";
    }
}