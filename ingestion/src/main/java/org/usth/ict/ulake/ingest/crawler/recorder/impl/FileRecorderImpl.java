package org.usth.ict.ulake.ingest.crawler.recorder.impl;

import java.io.File;
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
    private String path;
    private Map<Record, String> config;
    private byte[] buf = new byte[1024 * 1024];
    private Map<Record, String> log = new HashMap<>(); // log record

    /**
     * recorder config includes:
     * 1. path: String (path to stored local)
     */
    @Override
    public void setup(Map<Record, String> config) {
        this.config = config;
        path = (String) config.get(Record.PATH);
    }

    /**
     * this implement do not use store
     */
    @Override
    public void setup(Storage store) {
    }

    /**
     * record receive link to download into file
     * defined in config.
     *
     * meta structure:
     * 1. name: String (file name - option)
     * 2. link: String (link used to download)
     * 3. info: InputStream (data stream)
     *
     * NOTE:
     * if name is null or empty, file name will be defined
     * from link name
     */
    @Override
    public void record(InputStream data, Map<Record, String> meta) {
        InputStream is = data;
        String link = meta.get(Record.LINK);
        String name = meta.get(Record.NAME);
        TransferUtil transfer = new TransferUtil();

        if (name != null && !name.isEmpty()) {
            String[] paths = link.split("/");
            name = paths[paths.length - 1];
            name = name.strip();
        }

        Path path = Paths.get(this.path, name);
        File newFile = path.toFile();

        transfer.streamOutputFile(is, newFile, buf);

        // update log
        log.put(Record.PATH, path.toString());
        log.put(Record.NAME, name);

        Long fileSize = 0L;
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.put(Record.FILE_SIZE, fileSize.toString());
    }

    @Override
    public void info(Map<String, String> carrier, Map<Record, String> meta) {
        if (log == null)
            return;
        for (Record key : log.keySet())
            carrier.put(key.toString(), log.get(key));
    }
}
