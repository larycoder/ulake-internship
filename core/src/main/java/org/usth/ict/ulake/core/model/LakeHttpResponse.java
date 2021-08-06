package org.usth.ict.ulake.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LakeHttpResponse {
    ObjectMapper mapper = new ObjectMapper();
    final Map<Integer, String> codeMap;

    {
        codeMap = new HashMap<>();
        codeMap.put(200, "OK");
        codeMap.put(400, "Bad Request");
        codeMap.put(401, "Unauthorized");
        codeMap.put(403, "Forbidden");
        codeMap.put(404, "Not Found");
        codeMap.put(405, "Method Not Allowed");
    }

    int code;
    String msg;
    Object resp;

    public LakeHttpResponse() {
    }

    public LakeHttpResponse(int code) {
        this(code, null);
    }

    public LakeHttpResponse(int code, String msg) {
        this.code = code;
        this.msg = msg != null ? msg : codeMap.get(code);
    }

    public LakeHttpResponse(int code, String msg, Object resp) {
        this.code = code;
        if (msg != null && !msg.isEmpty()) {
            this.msg = msg;
        }
        else {
            this.msg = codeMap.get(code);
        }
        this.resp = resp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResp() {
        return resp;
    }

    public void setResp(Object resp) {
        this.resp = resp;
    }

    public Response build(int code, String msg, Object resp) {
        return Response.status(code).entity(toString(code, msg, resp)).build();
    }

    public Response build(int code, String msg) {
        return Response.status(code).entity(toString(code, msg)).build();
    }

    public Response build(int code) {
        return Response.status(code).entity(toString(code)).build();
    }

    public String toString(int code, String msg, Object resp) {
        JsonNode node = mapper.valueToTree(resp);
        try {
            return mapper.writeValueAsString(new LakeHttpResponse(code, msg, node));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public String toString(int code, String msg) {
        return toString(code, msg, null);
    }

    public String toString(int code) {
        return toString(code, null);
    }

    public String toString() {
        return toString(this.code, this. msg, this.resp);
    }
}
