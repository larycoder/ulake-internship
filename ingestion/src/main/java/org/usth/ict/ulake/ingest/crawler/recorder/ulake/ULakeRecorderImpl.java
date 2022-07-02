package org.usth.ict.ulake.ingest.crawler.recorder.ulake;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.model.ulake.StorageObjectModel;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ULakeRecorderImpl implements Recorder {
    String remoteHost;

    private ULakeRemote buildRemote() {
        ULakeRemote remote = null;
        try {
            remote = RestClientBuilder.newBuilder()
                    .baseUrl(new URL(remoteHost))
                    .build(ULakeRemote.class);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return remote;
    }

    @Override
    public void setup(HashMap config) {
        remoteHost = (String) config.get(Record.HOST);
    }

    @Override
    public void setup(Storage store) {
    }

    @Override
    public void record(Object info, HashMap meta) {
        InputStream file = (InputStream) info;
        var name = (String) meta.get(Record.NAME);
        var token = (String) meta.get(Record.TOKEN);
        var size = (Long) meta.get(Record.FILE_SIZE);

        // setup metadata
        Map metadata = new HashMap();
        metadata.put("name", name);
        metadata.put("length", size);

        if(token != null) {
            token = token.replaceAll("[\\n\\t ]", "");
            token = "Bearer " + token;
        } else {
            token = "Bearer missing.token";
        }

        StorageObjectModel model = new StorageObjectModel(metadata, file);
        ULakeRemote remote = buildRemote();

        try {
            Response resp = remote.uploadStorageObject(token, model);
            resp.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(Object carrier, HashMap meta) {
    }
}
