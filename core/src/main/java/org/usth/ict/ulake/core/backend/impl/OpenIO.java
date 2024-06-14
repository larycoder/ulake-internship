package org.usth.ict.ulake.core.backend.impl;

import com.google.protobuf.ByteString;
import io.openio.sds.Client;
import io.openio.sds.ClientBuilder;
import io.openio.sds.models.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.FileSystem;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class OpenIO implements FileSystem {
    private static final Logger log = LoggerFactory.getLogger(OpenIO.class);

    @ConfigProperty(name = "openio.core.endpoint")
    String endPointUrl;

    @ConfigProperty(name = "openio.core.namespace")
    String namespace;

    @ConfigProperty(name = "openio.core.account")
    String account;

    @ConfigProperty(name = "openio.core.bucket")
    String bucket;

    Client client;

    Client getClient() {
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
    public String create(String name, long length, ByteString is) {
        UUID uuid = UUID.randomUUID();
        OioUrl url = OioUrl.url(account, bucket, uuid.toString());
        log.info("Create: target {}, prepare to putObject url={} length={}", endPointUrl, url, length);
        ObjectInfo info = getClient().putObject(url, length, is.newInput());
        log.info("Created OpenIO object from stream. hash={}", info.hash());
        return uuid.toString();
    }

    @Override
    public String create(String rootDir, String name, long length, ByteString is) {
        return create(name, length, is);
    }

    @Override
    public String create(String rootDir, String name, long length, InputStream is) {
        return create(name, length, is);
    }

    @Override
    public boolean delete(String cid) {
        return false;
    }

    @Override
    public InputStream get(String cid) {
        OioUrl url = OioUrl.url(account, bucket, cid);
        Client client = getClient();
        ObjectInfo info = client.getObjectInfo(url);
        return client.downloadObject(info);
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

    @Override
    public Map<String, Object> stats() {
        // TODO: not considered for now
        return new HashMap<>();
    }

    @Override
    public boolean delete(String rootDir, String cid) {
        return false;
    }

    @Override
    public InputStream get(String rootDir, String cid) {
        return get(cid);
    }
}
