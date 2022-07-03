package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;

public class ULakeRecorderImpl implements Recorder<InputStream> {
    @Inject
    @RestClient
    DashboardService dashboardSvc;

    @Override
    public void setup(Map<Record, String> config) {
    }

    @Override
    public void setup(Storage store) {
    }

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        var name = meta.get(Record.NAME);
        var token = meta.get(Record.TOKEN);
        var size = Long.parseLong(meta.get(Record.FILE_SIZE));

        // setup metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", name);
        metadata.put("length", size);

        if (token != null) {
            token = token.replaceAll("[\\n\\t ]", "");
            token = "Bearer " + token;
        } else {
            token = "Bearer missing.token";
        }

        try {
            FileModel fileInfo = new FileModel();
            fileInfo.name = name;
            fileInfo.size = size;

            FileFormModel file = new FileFormModel();
            file.fileInfo = fileInfo;
            file.is = data;

            dashboardSvc.newFile(token, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(Map<String, String> carrier, Map<Record, String> meta) {
    }
}
