package org.usth.ict.ulake.lcc.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

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
