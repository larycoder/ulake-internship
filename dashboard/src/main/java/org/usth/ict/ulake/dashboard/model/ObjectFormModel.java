package org.usth.ict.ulake.dashboard.model;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ObjectFormModel {
    @FormParam("metadata")
    @PartType(MediaType.TEXT_PLAIN)
    public String metadata;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream is;
}
