package org.usth.ict.ingest.models;

import javax.persistence.*;

@Entity
@Table(name = "queue")
public class QueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;
    private String status;

    @ManyToOne
    @JoinColumn(name = "name_id", referencedColumnName = "id")
    private NameSpaceEntity name;
    @ManyToOne
    @JoinColumn(name = "crawl_id", referencedColumnName = "id")
    private CrawlInfoEntity info;

    public QueueEntity() {
    }

    public QueueEntity(Long id) {
        this.id = id;
    }

    public QueueEntity(
            Long id,
            NameSpaceEntity name,
            CrawlInfoEntity info,
            String link,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.link = link;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NameSpaceEntity getName() {
        return name;
    }

    public void setName(NameSpaceEntity name) {
        this.name = name;
    }

    public CrawlInfoEntity getInfo() {
        return info;
    }

    public void setInfo(CrawlInfoEntity info) {
        this.info = info;
    }
}
