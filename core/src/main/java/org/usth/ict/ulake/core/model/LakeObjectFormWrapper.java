package org.usth.ict.ulake.core.model;

import org.springframework.web.multipart.MultipartFile;

public class LakeObjectFormWrapper {
    private String metadata;
    private MultipartFile file;

    public LakeObjectFormWrapper() {
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
