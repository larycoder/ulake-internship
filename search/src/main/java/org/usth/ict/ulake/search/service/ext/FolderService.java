package org.usth.ict.ulake.search.service.ext;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.LakeServiceExceptionMapper;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;
import org.usth.ict.ulake.search.service.SearchService;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@RegisterProvider(LakeServiceExceptionMapper.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FolderService extends SearchService<UserFileSearchQuery> {
    @Override
    @POST
    @Path("/file/search")
    public LakeHttpResponse search(
        @HeaderParam("Authorization") String bearer,
        UserFileSearchQuery query) throws LakeServiceException;
}
