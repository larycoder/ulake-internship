package org.usth.ict.ulake.dashboard.model;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FileObjectFormModel {
    @FormParam("fileInfo")
    @PartType(MediaType.APPLICATION_JSON)
    public FileModel fileInfo;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream is;
}
