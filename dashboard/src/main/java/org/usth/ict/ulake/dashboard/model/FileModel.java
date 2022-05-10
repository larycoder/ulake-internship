package org.usth.ict.ulake.dashboard.model;

public class FileModel {
    public ObjectModel object;
    public String cid;
    public Long id;
    public String mime;
    public String name;
    public Long ownerId;
    public Long size;

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
}
