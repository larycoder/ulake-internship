package org.usth.ict.ulake.textr.clients;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.LakeServiceExceptionMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface FileRestClient {
    @GET
    @Path("/file/{ids}")
    @Schema(description = "get files by ids")
    LakeHttpResponse<List<FileModel>> fileList(@HeaderParam("Authorization") String bearer,
                                               @PathParam("ids")
                                         @Parameter(description = "File id to search") String ids);
    
    @DELETE
    @Path("/file/{fileId}")
    @Schema(description = "Delete a file")
    LakeHttpResponse<FileModel> deleteFile(@HeaderParam("Authorization") String bearer,
                                                  @PathParam("fileId") Long fileId);
}

