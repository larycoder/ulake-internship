package org.usth.ict.ulake.search.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;
import org.usth.ict.ulake.user.model.UserSearchQuery;

public class FilterModel {
    @Schema(description = "The filter of user information")
    public UserSearchQuery userQuery;

    @Schema(description = "The filter of file information")
    public UserFileSearchQuery fileQuery;
}
