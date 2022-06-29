package org.usth.ict.ulake.common.model.log;

public class LogModel {
    public Long id;
    public Long timestamp;
    public Long ownerId;
    public Long level;
    public String tag;
    public String service;
    public String content;

    public LogModel() {
    }

    public LogModel(Long id, Long timestamp, Long ownerId, Long level, String tag, String service, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.ownerId = ownerId;
        this.level = level;
        this.tag = tag;
        this.service = service;
        this.content = content;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getLevel() {
        return this.level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
