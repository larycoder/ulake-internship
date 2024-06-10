package org.usth.ict.ulake.textr.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.user.AuthModel;
import org.usth.ict.ulake.common.service.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AuthService {
    
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Inject
    @RestClient
    UserService userService;
    
    public ServiceResponseBuilder<?> login(AuthModel authModel) {
        logger.info("logging in to textr service");
        
        LakeHttpResponse<Object> loginResp = userService.getToken(authModel);
        
        return new ServiceResponseBuilder<>(loginResp.getCode(), loginResp.getMsg(), loginResp.getResp());
    }
}
