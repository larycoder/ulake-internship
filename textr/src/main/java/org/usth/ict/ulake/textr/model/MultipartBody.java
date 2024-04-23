package org.usth.ict.ulake.textr.model;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class MultipartBody {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    public String filename;
}