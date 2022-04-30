package org.usth.ict.query.model;

import org.usth.ict.query.model.query.OpModel;
import org.usth.ict.query.model.query.QueryException;
import org.usth.ict.query.model.query.Queryable;

public class FileModel implements Queryable {
    private ObjectModel object;
    private String cid;
    private Long id;
    private String mime;
    private String name;
    private Long ownerId;
    private Long size;

    public FileModel (
        ObjectModel object, String cid,
        Long id, String mime, String name,
        Long ownerId, Long size
    ) {
        this.object = object;
        this.cid = cid;
        this.id = id;
        this.mime = mime;
        this.name = name;
        this.ownerId = ownerId;
        this.size = size;
    }

    public ObjectModel getObject() {
        return object;
    }
    public void setObject(ObjectModel object) {
        this.object = object;
    }
    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        this.mime = mime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Boolean filter(String property, String value, OpModel op) {
        try {
            if (property.equals("cid")) {
                return op.verify(cid, value);
            } else if (property.equals("id")) {
                return op.verify(id, value);
            } else if (property.equals("mime")) {
                return op.verify(mime, value);
            } else if (property.equals("name")) {
                return op.verify(name, value);
            } else if (property.equals("ownerId")) {
                return op.verify(ownerId, value);
            } else if (property.equals("size")) {
                return op.verify(size, value);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
