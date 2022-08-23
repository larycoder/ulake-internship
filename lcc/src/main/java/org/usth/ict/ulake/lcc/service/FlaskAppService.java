package org.usth.ict.ulake.lcc.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.LakeServiceExceptionMapper;

@RegisterRestClient(configKey = "lcc-flaskapp-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
public interface FlaskAppService {
    @GET
    @Path("/detect/{patientFile}")
    public LakeHttpResponse<Object> predict(
        @PathParam("patientFile") String patientFile);
}
