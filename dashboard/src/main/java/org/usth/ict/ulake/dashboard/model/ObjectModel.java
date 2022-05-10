package org.usth.ict.ulake.dashboard.model;

public class ObjectModel {
    private Long id;
    private Long parentId;
    private Long accessTime;
    private Long createTime;
    private String cid;

    public ObjectModel(
        Long id, Long parentId, Long accessTime, Long createTime, String cid
    ) {
        this.id = id;
        this.parentId = parentId;
        this.accessTime = accessTime;
        this.createTime = createTime;
        this.cid = cid;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public Long getAccessTime() {
        return accessTime;
    }
    public void setAccessTime(Long accessTime) {
        this.accessTime = accessTime;
    }
    public Long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
}
