package org.usth.ict.ingest.resources.example;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.usth.ict.ingest.models.NameSpaceEntity;
import org.usth.ict.ingest.persistence.example.ExampleRepo;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hibernate")
public class HibernateFirstExample {

    @Inject
    ExampleRepo ss;

    @Path("/add/{table}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String addExp(@PathParam String table){
        System.out.println("Hello Hibernate");

        if(table.equals("name_spaces")) {
            // add name_spaces
            NameSpaceEntity ns = new NameSpaceEntity();
            ns.setName("test");
            ss.add(ns);
            return "Add test to NameSpace";
        }
        return "Add empty";
    }
}
