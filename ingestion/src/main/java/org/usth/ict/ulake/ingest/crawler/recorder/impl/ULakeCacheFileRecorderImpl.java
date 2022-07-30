package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.TransferUtil;

public class ULakeCacheFileRecorderImpl implements Recorder<InputStream> {

    private static final Logger sysLog = LoggerFactory.getLogger(ULakeCacheFileRecorderImpl.class);

    private Map<String, String> log = new HashMap<>();

    private Recorder<InputStream> file = new FileRecorderImpl();
    private Recorder<InputStream> ulake;

    public ULakeCacheFileRecorderImpl(DashboardService svc) {
        ulake = new ULakeRecorderImpl(svc);
    }

    @Override
    public void setup(Map<Record, String> config) {
        file.setup(config);
        ulake.setup(config);
    }

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        log.clear();

        // modify filename to avoid duplicate in temp folder
        String filename = meta.get(Record.FILE_NAME);
        meta.put(Record.FILE_NAME, filename + "_" + UUID.randomUUID());

        try {
            file.record(data, meta);
            sysLog.debug("Loaded file to temporary local...");
        } catch (Exception e) {
            e.printStackTrace();
            log.put(Record.STATUS.toString(), Boolean.valueOf(false).toString());
            return;
        }

        // prepare lake meta information
        var ulakeMeta = new HashMap<Record, String>();
        ulakeMeta.putAll(meta);
        for (var e : file.info().entrySet())
            ulakeMeta.put(Record.valueOf(e.getKey()), e.getValue());
        ulakeMeta.put(Record.FILE_NAME, filename);

        String pathFile = ulakeMeta.get(Record.FILE_PATH);
        try {
            // stream data to lake storage
            InputStream is = TransferUtil.streamFromFile(pathFile);
            ulake.record(is, ulakeMeta);
            if (is != null) is.close();
        } catch (Exception e) {
            e.printStackTrace();
            for (var entry : ulakeMeta.entrySet())
                log.put(entry.getKey().toString(), entry.getValue());
            log.put(Record.STATUS.toString(), Boolean.valueOf(false).toString());
            return;
        } finally {
            // close stream and delete temp
            new File(pathFile).delete();
        }

        sysLog.debug("Pushed local to lake storage...");
        log.putAll(ulake.info());
    }

    @Override
    public Map<String, String> info() {
        return new HashMap<String, String>(log);
    }
}
