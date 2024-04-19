package org.usth.ict.ulake.textr.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.textr.model.MultipartBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Path("/client")
public class MultipartClientResource {

    @POST
    @Path("/upload")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@MultipartForm MultipartBody body) throws IOException {
        byte[] byteContent = body.file.readAllBytes();
        String content = new String(byteContent, StandardCharsets.UTF_8);

        FileWriter file = new FileWriter("/home/malenquillaa/tmp/data/" + body.filename);
        file.write(content);
        file.close();

        return Response.ok().entity(content).build();
    }
}
