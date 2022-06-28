package org.usth.ict.ingest.crawler.recorder.ulake;

import org.usth.ict.ingest.crawler.recorder.FileRecorderImpl;
import org.usth.ict.ingest.crawler.recorder.Recorder;
import org.usth.ict.ingest.crawler.storage.Storage;
import org.usth.ict.ingest.models.macro.Record;
import org.usth.ict.ingest.utils.TransferUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ULakeCacheFileRecorderImpl implements Recorder {
    Recorder FileRecorder;
    Recorder ULakeRecorder;
    HashMap config;

    @Override
    public void setup(HashMap config) {
        this.config = config;

        var recorderSetting = new HashMap();
        recorderSetting.put(Record.PATH, config.get(Record.PATH));
        recorderSetting.put(Record.HOST, config.get(Record.HOST));

        FileRecorder = new FileRecorderImpl();
        FileRecorder.setup(recorderSetting);

        ULakeRecorder = new ULakeRecorderImpl();
        ULakeRecorder.setup(recorderSetting);
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
    public void record(Object info, HashMap meta) {
        FileRecorder.record(info, meta);

        var fileRecordDetail = new HashMap();
        FileRecorder.info(fileRecordDetail, null);
        fileRecordDetail.put(Record.TOKEN, meta.get(Record.TOKEN));

        TransferUtil util = new TransferUtil();
        InputStream is = util.streamFromFile(
                (String) fileRecordDetail.get(Record.PATH));

        ULakeRecorder.record(is, fileRecordDetail);
        if(is != null) {
            try {
                is.close();
            } catch(IOException e) {e.printStackTrace();}
        }
    }

    @Override
    public void info(Object carrier, HashMap meta) {
    }
}
