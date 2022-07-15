package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.TransferUtil;

public class FileRecorderImpl implements Recorder<InputStream, String> {
    private String path; // saving path
    private byte[] streamCache = new byte[1024 * 1024];
    private Map<String, String> log = new HashMap<>(); // log record

    @Override
    public void setup(Map<Record, String> config) {
        path = config.get(Record.FILE_PATH);
    }

    public void setup(Storage<String> store) {}

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        // file name
        Path path = Paths.get(this.path, meta.get(Record.FILE_NAME));
        File newFile = path.toFile();
        TransferUtil.streamOutputFile(data, newFile, streamCache);

        // update log
        Long fileSize = 0L;
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.put(Record.FILE_SIZE.toString(), fileSize.toString());
        log.put(Record.FILE_PATH.toString(), path.toString());
        log.put(Record.FILE_NAME.toString(), meta.get(Record.FILE_NAME));
    }

    @Override
    public Map<String, String> info() {
        return new HashMap<String, String>(log);
    }
}
