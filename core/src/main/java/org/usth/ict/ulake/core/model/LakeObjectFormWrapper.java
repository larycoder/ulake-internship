package org.usth.ict.ulake.core.model;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.ws.rs.core.MediaType;

public class LakeObjectFormWrapper {
    @RestForm
    private LakeObjectMetadata metadata;

    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private FileUpload file;

    public LakeObjectFormWrapper() {
    }

    public LakeObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(LakeObjectMetadata metadata) {
        this.metadata = metadata;
    }

    public FileUpload getFile() {
        return file;
    }

    public void setFile(FileUpload file) {
        this.file = file;
    }
}
