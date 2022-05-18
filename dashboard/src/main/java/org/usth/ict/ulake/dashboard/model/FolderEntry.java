package org.usth.ict.ulake.dashboard.model;

import java.util.List;

import org.usth.ict.ulake.common.model.folder.FileModel;

/**
 * Provide detail of entries relating to folder
 * including folder parent and children.
 * */
public class FolderEntry {
    public List<FolderInfo> subFolders;
    public List<FileModel> files;
}
