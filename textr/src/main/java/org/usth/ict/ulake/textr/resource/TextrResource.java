package org.usth.ict.ulake.textr.resource;

import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;
import org.usth.ict.ulake.textr.engine.IndexSearchEngine;
import org.usth.ict.ulake.textr.engine.IndexSearchEngineBenchmark;
import org.usth.ict.ulake.textr.model.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;


@Path("/textr")
@Produces(MediaType.APPLICATION_JSON)
public class TextrResource {

    private static final Logger LOG = Logger.getLogger(TextrResource.class);

    @Inject
    IndexSearchEngine indexSearchEngine;

    @Inject
    IndexSearchEngineBenchmark indexSearchEngineBenchmark;

    @Inject
    EntityManager entityManager;

//    Constructor
    public TextrResource() {
    }

//    Return all users from database in JSON format
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = User.listAll();
        LOG.info("Retrieved all users: " + users);
        return Response.ok(users).build();
    }

//    Return 1 user from database in JSON format
    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).build();
        }
        return Response.status(200).entity(user).build();
    }

//    Create new user
    @POST
    @Transactional
    @Path("/user/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(@PathParam("name") String name) {
        User user = new User();
        user.setName(name);
        entityManager.persist(user);
        return Response.status(200).entity("Added user " + name + " successfully to database").build();
    }

//    Delete 1 user with id
    @DELETE
    @Path("/user/{id}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(@PathParam("id") Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        entityManager.remove(user);
        return Response.status(200).entity("Deleted user " + id + " successfully from database").build();
    }

//    Update username with id
    @PUT
    @Path("/user/{id}/{name}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateUser(@PathParam("id") Long id, @PathParam("name") String name) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        user.setName(name);
        entityManager.persist(user);
        return Response.status(200).entity("Updated name to " + name + " user " + id + " successfully").build();
    }

//    Basic Lucene implementation for contents indexing and searching
    @GET
    @Path("/index")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response index() throws IOException {
//        Initialize index engine
        JsonObject out = indexSearchEngine.index();

        if (out.isEmpty())
            return Response.status(404).entity("No document found").build();
        return Response.status(200).entity(out).build();
    }

    @GET
    @Path("/search/{content}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@PathParam("content") String content) throws IOException {
//        Initialize search engine
        JsonObject out = indexSearchEngine.search(content);

        if (out.isEmpty())
            return Response.status(404).entity("No document found").build();
        return Response.status(200).entity(out.toString()).build();
    }

    @GET
    @Path("/benchmark/{iteration}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response benchmark(@PathParam("iteration") long iteration) throws IOException {
        if (iteration <= 0)
            return Response.status(404).build();

//        Init benchmark-initiator
        indexSearchEngineBenchmark = new IndexSearchEngineBenchmark();

        JsonObject out = indexSearchEngineBenchmark.startBenchmark(iteration);

        if (out.isEmpty())
            return Response.status(404).build();
        return Response.status(200).entity(out).build();
    }
}
