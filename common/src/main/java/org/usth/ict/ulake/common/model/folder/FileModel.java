package org.usth.ict.ulake.common.model.folder;

public class FileModel {
    public String cid;
    public Long id;
    public String mime;
    public String name;
    public Long ownerId;
    public Long size;
    public Long creationTime;
    public FolderModel parent;

    public FileModel(
        String cid, Long id, String mime,
        String name, Long ownerId,
        Long size, FolderModel parent, Long creationTime) {
        this.cid = cid;
        this.id = id;
        this.mime = mime;
        this.name = name;
        this.ownerId = ownerId;
        this.size = size;
        this.parent = parent;
        this.creationTime = creationTime;
    }

    public FileModel () {}
}
