package org.usth.ict.ulake.common.model.folder;

public class FolderModel {
    public Long id;
    public Long coreGroupId;
    public String name;
    public Long ownerId;

    public FolderModel() {}

    public FolderModel(Long id, Long coreGroupId, String name, Long ownerId) {
        this.id = id;
        this.coreGroupId = coreGroupId;
        this.name = name;
        this.ownerId = ownerId;
    }
}
