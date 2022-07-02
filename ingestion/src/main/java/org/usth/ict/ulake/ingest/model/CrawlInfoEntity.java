package org.usth.ict.ulake.ingest.model;

import org.usth.ict.ulake.ingest.utils.TransferUtil;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "crawl_info")
public class CrawlInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;
    @Column(columnDefinition = "TEXT")
    private String extra;
    @Column(columnDefinition = "TEXT")
    private String headers;

    @ManyToOne
    @JoinColumn(name = "name_id", referencedColumnName = "id")
    private NameSpaceEntity name;

    public CrawlInfoEntity() {
    }

    public CrawlInfoEntity(Long id) {
        this.id = id;
    }

    public CrawlInfoEntity(
            Long id, String link, String extra, NameSpaceEntity name
    ) {
        this.id = id;
        this.link = link;
        this.extra = extra;
        this.name = name;
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

    public Map getExtra() {
        if(this.extra == null || this.extra.isEmpty()) return null;
        TransferUtil util = new TransferUtil();
        Map data = util.stringToMap(this.extra);
        if(data == null) {
            Map raw = new HashMap();
            raw.put("raw", this.extra);
            return raw;
        }
        return data;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void setExtra(Map extra) {
        TransferUtil util = new TransferUtil();
        this.extra = util.mapToString(extra);
    }

    public NameSpaceEntity getName() {
        return name;
    }

    public void setName(NameSpaceEntity name) {
        this.name = name;
    }

    public Map getHeaders() {
        if(this.headers == null || this.headers.isEmpty()) return null;
        TransferUtil util = new TransferUtil();
        Map data = util.stringToMap(this.headers);
        if(data == null) {
            Map raw = new HashMap();
            raw.put("raw", this.extra);
            return raw;
        }
        return data;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public void setHeaders(Map headers) {
        TransferUtil util = new TransferUtil();
        this.headers = util.mapToString(headers);
    }
}
