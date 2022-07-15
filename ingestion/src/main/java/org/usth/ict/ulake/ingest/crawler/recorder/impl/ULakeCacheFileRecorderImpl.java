package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.IOException;
import java.io.InputStream;
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

    @Override
    public void setup(Map<Record, String> config) {
        file.setup(config);
        ulake.setup(config);
    }

    @Override
    public void setup(Storage<String> store) {}

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        file.record(data, meta);
        var fileMeta = new HashMap<Record, String>(meta);
        for (var entry : file.info().entrySet())
            fileMeta.put(Record.valueOf(entry.getKey()), entry.getValue());

        try {
            InputStream is = TransferUtil.streamFromFile(
                                 fileMeta.get(Record.PATH));
            ulake.record(is, fileMeta);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> info() {
        return null;
    }
}
