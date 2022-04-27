package org.usth.ict.ulake.folder.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.service.AclService;

@Path("/file")
@Tag(name = "File")
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {
    @Inject
    FileRepository repo;

    @Inject
    LakeHttpResponse response;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    AclService aclSvc;

    @GET
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all files")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get a single file info")
    public Response one(@PathParam("id") @Parameter(description = "File id to search") Long id) {
        AclModel acl = new AclModel();
        acl.setUserId(Long.parseLong(jwt.getName()));
        acl.setObjectId(id);
        acl.setIsGroup("0");
        acl.setIsFolder("0");
        acl.setPermission(PermissionModel.READ);
        var aclRst = aclSvc.isAllowed("bearer " + jwt.getRawToken(), acl);

        if (aclRst.getCode() == 200) {
            if ((Boolean) aclRst.getResp() == true)
                return response.build(200, null, repo.findById(id));
            else
                return response.build(403, "Permission fail");
        } else {
            return response.build(500, "Acl error", aclRst);
        }
    }

    @POST
    @Path("/search")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Search for files")
    public Response search(@RequestBody(description = "Query to perform search for user files") UserFileSearchQuery query) {
        var results = repo.search(query);
        if (results.isEmpty()) {
            return response.build(404);
        }
        return response.build(200, null, results);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new file info")
    @RolesAllowed({ "User", "Admin" })
    public Response post(UserFile entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a file info")
    public Response update(@PathParam("id") @Parameter(description = "File id to update") Long id,
                           @RequestBody(description = "New file info to save") UserFile newEntity) {
        return response.build(405);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a file info")
    public Response delete (@PathParam("id") @Parameter(description = "File id to delete") Long id) {
        UserFile entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}
