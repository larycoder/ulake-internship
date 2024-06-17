package org.usth.ict.ulake.common.model.core;

import java.io.InputStream;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ObjectFormModel {
    @FormParam("metadata")
    @PartType(MediaType.TEXT_PLAIN)
    public String metadata;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream is;
}
