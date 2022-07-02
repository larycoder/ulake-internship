package org.usth.ict.ulake.ingest.model.ulake;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Map;

public class StorageObjectModel {
    public StorageObjectModel(){}
    public StorageObjectModel(Map meta, InputStream file) {
        this.metadata = meta;
        this.file = file;
    }

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    public Map metadata;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;
}
