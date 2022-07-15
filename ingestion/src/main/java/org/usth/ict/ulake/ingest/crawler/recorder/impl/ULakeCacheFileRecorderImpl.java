package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.TransferUtil;

public class ULakeCacheFileRecorderImpl
    implements Recorder<InputStream, String> {

    private Recorder<InputStream, String> file = new FileRecorderImpl();
    private Recorder<InputStream, String> ulake = new ULakeRecorderImpl();
    private Map<String, String> log = new HashMap<>();

    @Override
    public void setup(Map<Record, String> config) {
        file.setup(config);
        ulake.setup(config);
    }

    @Override
    public void setup(Storage<String> store) {}

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        try {
            file.record(data, meta);

            // prepare lake meta information
            var ulakeMeta = new HashMap<Record, String>();
            for (var e : file.info().entrySet())
                ulakeMeta.put(Record.valueOf(e.getKey()), e.getValue());

            // stream data to lake storage
            String path = Paths.get(ulakeMeta.get(Record.FILE_PATH),
                                    ulakeMeta.get(Record.FILE_NAME)).toString();
            InputStream is = TransferUtil.streamFromFile(path);
            ulake.record(is, ulakeMeta);

            log.putAll(ulake.info());
            is.close();
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
