package org.usth.ict.ulake.core.backend.impl;

import org.springframework.beans.factory.annotation.Value;
import org.usth.ict.ulake.core.backend.FileSystem;

import java.io.InputStream;
import java.util.List;

public class OpenIO implements FileSystem {
    @Value("${openio.core.endpoint")
    private String endPointUrl;

    @Value("${openio.core.key-id")
    private String keyId;

    @Value("${openio.core.access-key")
    private String accessKey;

    @Override
    public String create(InputStream is) {
        return null;
    }

    @Override
    public boolean delete(String cid) {
        return false;
    }

    @Override
    public InputStream get(String cid) {
        return null;
    }

    @Override
    public List<String> ls(String dir) {
        return null;
    }

    @Override
    public String mkdir(String name) {
        return null;
    }

    // getters, setters

    public String getEndPointUrl() {
        return endPointUrl;
    }

    public void setEndPointUrl(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}
