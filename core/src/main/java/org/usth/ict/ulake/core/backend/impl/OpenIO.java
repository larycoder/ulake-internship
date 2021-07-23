package org.usth.ict.ulake.core.backend.impl;

import io.openio.sds.Client;
import io.openio.sds.ClientBuilder;
import io.openio.sds.models.ContainerInfo;
import io.openio.sds.models.OioUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.usth.ict.ulake.core.backend.FileSystem;

import java.io.InputStream;
import java.util.List;

@Component
public class OpenIO implements FileSystem {
    @Value("${openio.core.endpoint}")
    private String endPointUrl;

    @Value("${openio.core.namespace}")
    private String namespace;

    @Value("${openio.core.account}")
    private String account;

    private Client client;

    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient(namespace, endPointUrl);
        }
        return client;
    }

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
        System.out.println(account);
        OioUrl url = OioUrl.url(account, name);
        ContainerInfo ci = getClient().createContainer(url);
        System.out.println("Container is is " + ci);
        return ci.name();
    }
}
