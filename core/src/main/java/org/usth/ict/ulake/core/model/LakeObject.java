package org.usth.ict.ulake.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = @Index(columnList = "cid"))
public class LakeObject {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Long parentId;
    private String cid;
    private Long createTime;
    private Long accessTime; // ?

    @ManyToOne
    @JoinColumn
    private LakeGroup group;

    public LakeObject() { }

    public LakeObject(String cid) {
        this.cid = cid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Long accessTime) {
        this.accessTime = accessTime;
    }

    public LakeGroup getGroup() {
        return group;
    }

    public void setGroup(LakeGroup group) {
        this.group = group;
    }


}
