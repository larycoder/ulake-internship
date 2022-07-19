package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
        try {
            // TODO: modify filename to avoid duplicate
            file.record(data, meta);

            sysLog.debug("Loaded file to temporary local...");

            // prepare lake meta information
            var ulakeMeta = new HashMap<Record, String>();
            for (var e : file.info().entrySet())
                ulakeMeta.put(Record.valueOf(e.getKey()), e.getValue());

            // stream data to lake storage
            String pathFile = ulakeMeta.get(Record.FILE_PATH);
            InputStream is = TransferUtil.streamFromFile(pathFile);
            ulake.record(is, ulakeMeta);

            sysLog.debug("Pushed local to lake storage...");

            log.putAll(ulake.info());

            // clean temporary file
            if (is != null) is.close();
            new File(pathFile).delete();

        } catch (IOException e) {
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
