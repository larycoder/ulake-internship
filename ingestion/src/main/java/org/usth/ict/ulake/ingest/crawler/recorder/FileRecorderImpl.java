package org.usth.ict.ulake.ingest.crawler.recorder;

import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.utils.TransferUtil;
import org.usth.ict.ulake.ingest.model.macro.Record;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileRecorderImpl implements Recorder {
    private String path;
    private Map config;
    private byte[] buf = new byte[Record.MAX];
    private Map log = new HashMap(); // record previous record action

    /**
     * recorder config includes:
     * 1. path: String (path to stored local)
     */
    @Override
    public void setup(HashMap config) {
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
    public void record(Object info, HashMap meta) {
        InputStream is = (InputStream) info;
        String link = (String) meta.get(Record.LINK);
        String name = (String) meta.get(Record.NAME);
        TransferUtil transfer = new TransferUtil();

        if (name == null && name.isEmpty()) {
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
        } catch(IOException e) {
            e.printStackTrace();
        }
        log.put(Record.FILE_SIZE, fileSize);
    }

    @Override
    public void info(Object carrier, HashMap meta) {
        var mapCarrier = (Map<Object, Object>) carrier;
        if(log != null) {
            mapCarrier.putAll(log);
        }
    }
}
