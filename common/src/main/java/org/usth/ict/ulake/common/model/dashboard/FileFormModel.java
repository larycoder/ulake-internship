package org.usth.ict.ulake.common.model.dashboard;

import java.io.InputStream;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.usth.ict.ulake.common.model.folder.FileModel;

public class FileFormModel {
    @FormParam("fileInfo")
    @PartType(MediaType.APPLICATION_JSON)
    public FileModel fileInfo;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream is;
}
