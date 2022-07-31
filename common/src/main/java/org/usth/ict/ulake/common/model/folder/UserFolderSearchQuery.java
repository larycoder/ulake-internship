package org.usth.ict.ulake.common.model.folder;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserFolderSearchQuery {
    @Schema(description = "Filter by folder ids")
    public List<Long> ids;

    @Schema(description = "Filter by file ownerId")
    public List<Long> ownerIds;

    @Schema(description = "A keyword that responded folder names must contain.")
    public String keyword;

    public UserFolderSearchQuery () {}
}
