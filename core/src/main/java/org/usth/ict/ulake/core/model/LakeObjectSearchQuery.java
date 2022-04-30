package org.usth.ict.ulake.core.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class LakeObjectSearchQuery {
    @Schema
    public String keyword;

    @Schema
    public Long minCreateTime;

    @Schema
    public Long maxCreateTime;

    @Schema
    public Long minAccessTime;

    @Schema
    public Long maxAccessTime;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getMinCreateTime() {
        return minCreateTime;
    }

    public void setMinCreateTime(Long minCreateTime) {
        this.minCreateTime = minCreateTime;
    }

    public Long getMaxCreateTime() {
        return maxCreateTime;
    }

    public void setMaxCreateTime(Long maxCreateTime) {
        this.maxCreateTime = maxCreateTime;
    }

    public Long getMinAccessTime() {
        return minAccessTime;
    }

    public void setMinAccessTime(Long minAccessTime) {
        this.minAccessTime = minAccessTime;
    }

    public Long getMaxAccessTime() {
        return maxAccessTime;
    }

    public void setMaxAccessTime(Long maxAccessTime) {
        this.maxAccessTime = maxAccessTime;
    }
}
