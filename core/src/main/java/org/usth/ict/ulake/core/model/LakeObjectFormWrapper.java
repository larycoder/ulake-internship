package org.usth.ict.ulake.core.model;

import io.vertx.ext.web.FileUpload;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class LakeObjectFormWrapper {
    @FormParam("metadata")
    private LakeObjectMetadata metadata;

    @FormParam("file")
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
