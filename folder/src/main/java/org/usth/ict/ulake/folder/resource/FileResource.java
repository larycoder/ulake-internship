package org.usth.ict.ulake.folder.resource;

import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.persistence.FileRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/file")
public class FileResource {
    @Inject
    FileRepository repo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserFile> all() {
        return repo.listAll();
    }
}