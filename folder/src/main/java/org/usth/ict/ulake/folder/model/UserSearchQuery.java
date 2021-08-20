package org.usth.ict.ulake.folder.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserSearchQuery {
    @Schema(description = "A keyword that responded filenames must contain.")
    public String keyword;

    @Schema(description = "Minimum size. -1 means ignored.")
    public Long minSize;

    @Schema(description = "Maximum size. -1 means ignored.")
    public Long maxSize;

    @Schema(description = "MIME keyword that the file MIME must contain.")
    public String mime;
}
