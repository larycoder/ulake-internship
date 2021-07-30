package org.usth.ict.ulake.core.backend.impl;

import io.openio.sds.Client;
import io.openio.sds.ClientBuilder;
import io.openio.sds.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.usth.ict.ulake.core.backend.FileSystem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OpenIO implements FileSystem {
    private static final Logger log = LoggerFactory.getLogger(OpenIO.class);

    @Value("${openio.core.endpoint}")
    private String endPointUrl;

    @Value("${openio.core.namespace}")
    private String namespace;

    @Value("${openio.core.account}")
    private String account;

    @Value("${openio.core.bucket}")
    private String bucket;

    private Client client;

    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient(namespace, endPointUrl);
        }
        return client;
    }

    @Override
    public String create(String name, long length, InputStream is) {
        UUID uuid = UUID.randomUUID();
        OioUrl url = OioUrl.url(account, bucket, uuid.toString());
        log.info("Create: target {}, prepare to putObject url={} length={}", endPointUrl, url, length);
        ObjectInfo info = getClient().putObject(url, length, is);
        log.info("Created OpenIO object from stream. hash={}", info.hash());
        return uuid.toString();
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
        OioUrl url = OioUrl.url(account, dir);
        ListOptions options = new ListOptions();
        ObjectList objectList = getClient().listObjects(url, options);
        List<ObjectList.ObjectView> list = objectList.objects();
        ArrayList<String> ret = new ArrayList<>();
        for (var obj: list) {
            ret.add(obj.name());
        }
        return ret;
    }

    @Override
    public String mkdir(String name) {
        System.out.println(account);
        OioUrl url = OioUrl.url(account, name);
        ContainerInfo ci = getClient().createContainer(url);
        log.info("Created container {}", ci);
        return ci.name();
    }
}
