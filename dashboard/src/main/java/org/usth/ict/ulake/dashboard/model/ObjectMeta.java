package org.usth.ict.ulake.dashboard.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ObjectMeta {
    @Schema(description = "upload file name")
    public String name;

    @Schema(description = "group id of file")
    public String groupId;

    @Schema(description = "size of file count in byte")
    public Long length;
}
