package org.usth.ict.ulake.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class LakeHttpResponse<T> {
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
        codeMap.put(409, "Conflict");
        codeMap.put(415, "Unsupported Media Type");
    }

    int code;
    String msg;
    T resp;

    public LakeHttpResponse(int code, String msg, T resp) {
        this.code = code;
        this.msg = msg;
        this.resp = resp;
    }

    public LakeHttpResponse() { }

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

    public T getResp() {
        return resp;
    }

    public void setResp(T resp) {
        this.resp = resp;
    }

    public Response build(int code, String msg, T resp, Map.Entry<String, String> header) {
        ResponseBuilder rb = Response.status(code).entity(toString(code, msg, resp));
        rb.header(header.getKey(), header.getValue());
        return rb.build();
    }

    public Response build(int code, String msg, T resp, Map<String, String> headers) {
        ResponseBuilder rb = Response.status(code).entity(toString(code, msg, resp));
        for (var header : headers.entrySet()) {
            rb.header(header.getKey(), header.getValue());
        }
        return rb.build();
    }

    public Response build(int code, String msg, List<T> resp) {
        return Response.status(code).entity(toString(code, msg, resp)).build();
    }

    public Response build(int code, String msg, T resp) {
        return Response.status(code).entity(toString(code, msg, resp)).build();
    }

    public Response build(int code, String msg) {
        T ret = null;
        return build(code, msg, ret);
    }

    public Response build(int code) {
        return build(code, null);
    }

    // this.code = code;
    // if (msg != null && !msg.isEmpty()) {
    //     this.msg = msg;
    // } else {
    //     this.msg = codeMap.get(code);
    // }
    // //this.resp =
    // var converted = mapper.valueToTree(resp);
    // try {
    //     this.resp = (T) converted;
    // } catch (Exception e) {
    //     return "{}";
    // }

    public String toString(int code, String msg, T resp) {
        try {
            return mapper.writeValueAsString(new LakeHttpResponse<T>(code, msg, resp));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public String toString(int code, String msg, List<T> resp) {
        try {
            return mapper.writeValueAsString(new LakeHttpResponse<List<T>>(code, msg, resp));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

}