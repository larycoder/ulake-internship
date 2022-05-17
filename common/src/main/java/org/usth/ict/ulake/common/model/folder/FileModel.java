package org.usth.ict.ulake.common.model.folder;

public class FileModel {
    public String cid;
    public Long id;
    public String mime;
    public String name;
    public Long ownerId;
    public Long size;

    public FileModel (String cid, Long id, String mime,
                      String name, Long ownerId, Long size) {
        this.cid = cid;
        this.id = id;
        this.mime = mime;
        this.name = name;
        this.ownerId = ownerId;
        this.size = size;
    }

    public FileModel () {
        this.cid = null;
        this.id = null;
        this.mime = null;
        this.name = null;
        this.ownerId = null;
        this.size = null;
    }
}
