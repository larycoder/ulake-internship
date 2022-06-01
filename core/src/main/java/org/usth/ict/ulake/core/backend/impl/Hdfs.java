package org.usth.ict.ulake.core.backend.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageStatistics;
import org.apache.hadoop.fs.DU;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class Hdfs implements org.usth.ict.ulake.core.backend.FileSystem {
    private static final Logger log = LoggerFactory.getLogger(Hdfs.class);

    @ConfigProperty(name = "hdfs.core.namenode")
    String namenodeUri;

    @ConfigProperty(name = "hdfs.core.root")
    String rootDir;

    FileSystem client;

    FileSystem getClient() {
        if(client == null) {
            try {
                Configuration conf = new Configuration();
                client = FileSystem.get(new URI(namenodeUri), conf);
            } catch(URISyntaxException e) {
                log.error("Namenode URI error: {}", e);
            } catch (IOException e) {
                log.error("Namenode connection error: {}", e);
            }
        }
        return client;
    }

    @Override
    public String create(String name, long length, InputStream is) {
        UUID uuid = UUID.randomUUID();
        String pathFile = Paths.get(rootDir, uuid.toString()).toString();
        Path fullPath = new Path(
                namenodeUri + Paths.get("/", pathFile).toString());

        try{
            OutputStream os = getClient().create(fullPath);
            log.info("Create: target {}, prepare to putObject url={} length={}",
                    namenodeUri, pathFile, length);
            byte buff[] = new byte[4096];
            int len;
            try {
                while((len = is.read(buff)) > 0) os.write(buff, 0, len);
            } catch(IOException e) {
                log.error("Error to stream data to {}: {}", fullPath, e);
                return null;
            } finally {
                os.close();
                is.close();
            }
        } catch(IOException e) {
            log.error("Fail to create new file {}: {}", fullPath, e);
            return null;
        }

        // TODO extract file info from from hdfs
        log.info("Created Hdfs object from stream. No file info yet");

        return uuid.toString();
    }

    @Override
    public boolean delete(String cid) {
        return false;
    }

    @Override
    public InputStream get(String cid) {
        String pathFile = namenodeUri + Paths.get("/" , rootDir, cid).toString();
        try {
            InputStream is = getClient().open(new Path(pathFile));
            return is;
        } catch(IOException e) {
            log.error("Fail to open file {}: {}", pathFile, e);
            return null;
        }
    }

    @Override
    public List<String> ls(String dir) {
        String path = namenodeUri + Paths.get("/", rootDir, dir).toString();
        try {
            List<String> fileList = new ArrayList<String>();

            for(FileStatus status : getClient().listStatus(new Path(path))) {
                fileList.add(status.getPath().toString());
            }
            return fileList;
        } catch(IOException e) {
            log.error("Fail to list file {}: {}", path, e);
            return null;
        }
    }

    @Override
    public String mkdir(String name) {
        // TODO Implementation
        return null;
    }

    @Override
    public Map<String, Object> stats() {
        var ret = new HashMap<String, Object>();
        var fsStats = getClient().getStorageStatistics();
        Iterator<StorageStatistics.LongStatistic> it = fsStats.getLongStatistics();
        while (it.hasNext()) {
            StorageStatistics.LongStatistic next = it.next();
            ret.put(next.getName(), next.getValue());            
        }
        return ret;
    }

}
