package org.usth.ict.ulake.dashboard.model.extension;

public class ExtensionModel<T> {
    private Integer code;
    private String msg;
    private T resp;

    public ExtensionModel(Integer code, String msg, T resp) {
        this.code = code;
        this.msg = msg;
        this.resp = resp;
    }

    public ExtensionModel() {
        this.code = null;
        this.msg = null;
        this.resp = null;
    }

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
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
}
