package org.usth.ict.ulake.dashboard.model;

/**
 * Hold only folder info without detail of
 * subfolder and files inside it.
 * */
public class FolderInfo {
    public Long id;
    public String name;
    public String ownerId;

    public FolderInfo() {}

    public FolderInfo(Long id, String name, String ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }
}
