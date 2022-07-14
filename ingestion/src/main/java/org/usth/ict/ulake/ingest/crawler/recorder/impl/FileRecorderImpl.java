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

public class FileRecorderImpl implements Recorder<InputStream> {
    private String path; // saving path
    private byte[] streamCache = new byte[1024 * 1024];
    private Map<String, String> log = new HashMap<>(); // log record

    @Override
    public void setup(Map<Record, String> config) {
        path = config.get(Record.PATH);
    }

    public void setup(Storage<String> store) {}

    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        // file name
        String name = meta.get(Record.NAME);
        String link = meta.get(Record.LINK);

        if (name == null || name.isEmpty()) {
            String[] paths = link.split("/");
            name = paths[paths.length - 1];
            name = name.strip();
        }

        Path path = Paths.get(this.path, name);
        File newFile = path.toFile();
        TransferUtil.streamOutputFile(data, newFile, streamCache);

        // update log
        log.put(Record.PATH.toString(), path.toString());
        log.put(Record.NAME.toString(), name);
        Long fileSize = 0L;
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.put(Record.FILE_SIZE.toString(), fileSize.toString());
    }

    @Override
    public Map<String, String> info() {
        return new HashMap<String, String>(log);
    }
}
