package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.model.macro.Record;

public class ULakeRecorderImpl implements Recorder<InputStream> {
    private static final Logger sysLog = LoggerFactory.getLogger(ULakeRecorderImpl.class);
    private Map<String, String> log = new HashMap<>();
    private FileModel fileInfo = new FileModel();
    private String tokenAuth;
    private DashboardService dashboardSvc;

    public ULakeRecorderImpl(DashboardService svc) {
        dashboardSvc = svc;
    }

    @Override
    public void setup(Map<Record, String> config) {
        tokenAuth = config.get(Record.TOKEN);

        // folder holding crawled files
        if (config.get(Record.STORAGE_DIR) != null) {
            var dir = new FolderModel();
            dir.id = Long.parseLong(config.get(Record.STORAGE_DIR));
            fileInfo.parent = dir;
        }
    }

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        try {
            fileInfo.name = meta.get(Record.FILE_NAME);
            fileInfo.size = Long.parseLong(meta.get(Record.FILE_SIZE));

            FileFormModel file = new FileFormModel();
            file.fileInfo = fileInfo;
            file.is = data;

            sysLog.debug("crawl token: " + tokenAuth);

            var resp = dashboardSvc.newFile(tokenAuth, file).getResp();
            log.put(Record.OBJECT_ID.toString(), resp.id.toString());
            log.put(Record.STATUS.toString(), Boolean.valueOf(true).toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.put(Record.STATUS.toString(), Boolean.valueOf(false).toString());
        }
    }

    @Override
    public Map<String, String> info() {
        return new HashMap<String, String>(log);
    }
}
