package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.TransferUtil;

public class ULakeCacheFileRecorderImpl implements Recorder<InputStream> {
    private Recorder<InputStream> fileRecorder;
    private Recorder<InputStream> ulakeRecorder;
    private Map<Record, String> config;

    @Override
    public void setup(Map<Record, String> config) {
        this.config = config;

        var recorderSetting = new HashMap<Record, String>();
        recorderSetting.put(Record.PATH, config.get(Record.PATH));

        fileRecorder = new FileRecorderImpl();
        fileRecorder.setup(recorderSetting);

        ulakeRecorder = new ULakeRecorderImpl();
        ulakeRecorder.setup(recorderSetting);
    }

    @Override
    public void setup(Storage store) {
    }

    /**
     *
     * Note:
     * meta includes:
     *  1. LINK
     *  2. NAME
     *  3. TOKEN
     *
     */
    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        fileRecorder.record(data, meta);

        var fileRecordLog = new HashMap<String, String>();
        fileRecorder.info(fileRecordLog, null);

        var fileRecordDetail = new HashMap<Record, String>();
        for (String key : fileRecordLog.keySet()) {
            Record newKey = Record.valueOf(key);
            fileRecordDetail.put(newKey, fileRecordLog.get(key));
        }
        fileRecordDetail.put(Record.TOKEN, meta.get(Record.TOKEN));

        TransferUtil util = new TransferUtil();
        InputStream is = util.streamFromFile(
                             (String) fileRecordDetail.get(Record.PATH));

        ulakeRecorder.record(is, fileRecordDetail);
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void info(Map<String, String> carrier, Map<Record, String> meta) {
    }
}
