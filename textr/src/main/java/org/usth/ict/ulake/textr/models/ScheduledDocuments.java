package org.usth.ict.ulake.textr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@Table(name = "del_scheduled_docs")
public class ScheduledDocuments {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted_date")
    private Date deletedDate = new Date();

    @Column(name = "permanent_days")
    private Integer permanentDelDays;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doc_id", referencedColumnName = "id", unique = true)
    private Documents doc;

    public ScheduledDocuments(Documents doc, Integer permanentDelDays) {
        this.doc = doc;
        this.permanentDelDays = permanentDelDays;
    }
}
