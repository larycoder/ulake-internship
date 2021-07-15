package org.usth.ict.ulake.core.model;

import javax.persistence.*;

@Entity
public class StorageObject {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupObject group;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public GroupObject getGroup() {
        return group;
    }

    public void setGroup(GroupObject group) {
        this.group = group;
    }
}
