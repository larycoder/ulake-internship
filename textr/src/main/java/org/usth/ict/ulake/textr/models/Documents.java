package org.usth.ict.ulake.textr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "documents", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Documents {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotNull
    @Column(name = "name")
    private String name;
    
    @NotNull
    @Column(name = "path")
    private String path;
    
    @NotNull
    @JsonIgnore
    @Column(name = "status")
    private EDocStatus status;
    
    public Documents(String name, String path, EDocStatus status) {
        this.name = name;
        this.status = status;
        this.path = path;
    }
}
