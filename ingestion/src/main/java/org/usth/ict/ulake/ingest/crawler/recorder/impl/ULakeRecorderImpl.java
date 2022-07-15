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

public class ULakeRecorderImpl implements Recorder<InputStream, String> {
    private Map<String, String> log = new HashMap<>();
    private String tokenAuth;
    private FileModel fileInfo;

    @Inject
    @RestClient
    DashboardService dashboardSvc;

    @Override
    public void setup(Map<Record, String> config) {
        tokenAuth = config.get(Record.TOKEN);
        // TODO: setup default file info
    }

    @Override
    public void setup(Storage<String> store) {}

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        var name = meta.get(Record.FILE_NAME);
        var size = Long.parseLong(meta.get(Record.FILE_SIZE));

        // setup metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", name);
        metadata.put("length", size);

        try {
            fileInfo.name = name;
            fileInfo.size = size;

            FileFormModel file = new FileFormModel();
            file.fileInfo = fileInfo;
            file.is = data;

            dashboardSvc.newFile(tokenAuth, file);
            log.put(Record.STATUS.toString(),
                    Boolean.valueOf(true).toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.put(Record.STATUS.toString(),
                    Boolean.valueOf(false).toString());
        }
    }

    @Override
    public Map<String, String> info() {
        return new HashMap<String, String>(log);
    }
}
