package org.usth.ict.ingest.models;

import javax.naming.Name;
import javax.persistence.*;

@Entity
@Table(name = "name_spaces")
public class NameSpaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public NameSpaceEntity() {
    }

    public NameSpaceEntity(Long id) {
        this.id = id;
    }

    public NameSpaceEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
