package org.usth.ict.ulake.core.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import org.usth.ict.ulake.common.misc.Utils;

import java.util.HashMap;
import java.util.Map;

public class LakeHttpResponse {
    public static final Map<Integer, String> codeMap;

    static {
        codeMap = new HashMap<>();
        codeMap.put(200, "OK");
        codeMap.put(400, "Bad Request");
        codeMap.put(401, "Unauthorized");
        codeMap.put(403, "Forbidden");
        codeMap.put(405, "Method Not Allowed");
    }

    @Expose private int code;
    @Expose private String msg;
    @Expose private Object resp;

    public LakeHttpResponse(int code, String msg) {
        this.code = code;
        this.msg = msg != null ? msg : codeMap.get(code);
    }

    public LakeHttpResponse(int code, String msg, Object resp) {
        this.code = code;
        this.msg = msg != null ? msg : codeMap.get(code);
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

    public static String toString(int code, String msg, Object resp) {
//        JsonElement jsonElement = Utils.gsonNoExpose().toJsonTree(resp);
//        return Utils.gsonNoExpose().toJson(new LakeHttpResponse(code, msg, jsonElement));
        // TODO
        return "";
    }

    public static String toString(int code, String msg) {
        return ""; // TODO Utils.gsonNoExpose().toJson(new LakeHttpResponse(code, msg, null));
    }

    public static String toString(int code) {
        return ""; // TODO Utils.gsonNoExpose().toJson(new LakeHttpResponse(code, null));
    }
}
