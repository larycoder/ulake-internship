package org.usth.ict.ulake.textr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

@Getter
@Setter
@AllArgsConstructor
public class ServiceResponseBuilder<T> {
    
    @NotNull
    private int status;
    
    private String msg = null;
    
    private T resp = null;
    
    public ServiceResponseBuilder(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    
    public ServiceResponseBuilder(int status, T resp) {
        this.status = status;
        this.resp = resp;
    }
    
    public Response build() {
        return Response.status(status).entity(this.toString()).build();
    }
    
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
