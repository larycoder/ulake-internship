package org.usth.ict.ulake.core.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = @Index(columnList = "cid"))
public class LakeObject  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public Long parentId;
    public String cid;
    public Long createTime;
    public Long accessTime; // ?

    @ManyToOne
    @JoinColumn
    public LakeGroup group;

    public LakeObject() { }

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
