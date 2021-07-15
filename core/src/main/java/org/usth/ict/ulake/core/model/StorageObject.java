package org.usth.ict.ulake.core.model;

import javax.persistence.*;

@Entity
public class StorageObject {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String cid;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public StorageObject() {
    }

    public StorageObject(String cid) {
        this.cid = cid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

}
