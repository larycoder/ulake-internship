package org.usth.ict.ulake.core.model;

import io.vertx.ext.web.FileUpload;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class LakeObjectFormWrapper {
    @FormParam("metadata")
    @PartType(MediaType.TEXT_PLAIN)
    public String metadata;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload file;

    public LakeObjectFormWrapper() {
    }
}
