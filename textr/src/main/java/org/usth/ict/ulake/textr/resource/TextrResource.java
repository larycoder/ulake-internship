package org.usth.ict.ulake.textr.resource;

import org.jboss.logging.Logger;
import org.usth.ict.ulake.textr.engine.IndexSearchEngine;
import org.usth.ict.ulake.textr.engine.Lucene;
import org.usth.ict.ulake.textr.model.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@Path("/textr")
@Produces(MediaType.APPLICATION_JSON)
public class TextrResource {

    private static final Logger LOG = Logger.getLogger(TextrResource.class);

    private final Lucene lucene;

    @Inject
    IndexSearchEngine indexSearchEngine;

    @Inject
    EntityManager entityManager;

//    Constructor
    public TextrResource() throws IOException {
        lucene = new Lucene();
    }

//    Return all users from database in JSON format
    @GET
    @Path("/listAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = User.listAll();
        LOG.info("Retrieved all users: " + users);
        return Response.ok(users).build();
    }

//    Return 1 user from database in JSON format
    @GET
    @Path("/listByID/{id}")
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
    @Path("/createUser/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(@PathParam("name") String name) {
        User user = new User();
        user.setName(name);
        entityManager.persist(user);
        return Response.status(200).entity("Added user " + name + " successfully to database").build();
    }

//    Delete 1 user with id
    @DELETE
    @Path("/deleteUser/{id}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(@PathParam("id") Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        entityManager.remove(user);
        return Response.status(200, "Deleted user " + id + " successfully from database").build();
    }

//    Update username with id
    @PUT
    @Path("/updateUser/{id}/{name}")
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response index() throws IOException {
//        Initialize index engine
        int numIndexed = indexSearchEngine.index(lucene);

        return Response.status(200).entity("Indexed " + numIndexed + " documents").build();
    }

    @GET
    @Path("/search/{content}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response search(@PathParam("content") String content) throws IOException {
//        Initialize search engine
        HashMap<String, Float> filesMap = indexSearchEngine.search(lucene, content);

        if (filesMap.isEmpty())
            return Response.status(404).entity("No document found").build();
        return Response.status(200).entity(filesMap.toString()).build();
    }
}
