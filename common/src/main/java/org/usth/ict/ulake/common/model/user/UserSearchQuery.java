package org.usth.ict.ulake.common.model.user;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserSearchQuery {
    @Schema(description = "List of user ids allowed")
    public List<Long> ids;

    @Schema(description = "List of keywords in user name or email or first name, last name")
    public List<String> keywords;

    @Schema(description = "List of group ids to search users from")
    public List<Integer> groups;

    @Schema(description = "List of department ids to search users from")
    public List<Integer> departments;

    @Schema(description = "Minimum register time (epoch). -1 means ignored.")
    public Long minRegisterTime;

    @Schema(description = "Maximum register time (epoch). -1 means ignored.")
    public Long maxRegisterTime;

    public UserSearchQuery(){}
}
