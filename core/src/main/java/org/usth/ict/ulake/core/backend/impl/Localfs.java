package org.usth.ict.ulake.core.backend.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class Localfs implements org.usth.ict.ulake.core.backend.FileSystem {
    private static final Logger log = LoggerFactory.getLogger(Localfs.class);

    @ConfigProperty(name = "localfs.core.root")
    String rootDir;

    @Override
    public String create(String name, long length, InputStream is) {
        return create(rootDir, name, length, is);
    }

    @Override
    public String create(String rootDir, String name, long length, InputStream is) {
        UUID uuid = UUID.randomUUID();
        Path path = Paths.get("/", rootDir, uuid.toString());
        try {
            OutputStream os = new FileOutputStream(path.toString());
            try {
                is.transferTo(os);
            } catch (IOException e) {
                log.error("Error to stream data to {}: {}", path, e);
                return null;
            } finally {
                os.close();
                is.close();
            }
        } catch (IOException e) {
            log.error("Fail to create new file {}: {}", path, e);
            return null;
        }
        return uuid.toString();
    }

    @Override
    public boolean delete (String rootDir, String cid) {
        Path path = Paths.get("/", rootDir, cid);
        try {
            File file = new File(path.toString());
            if (!file.delete())
                throw new IOException("File delete action fail.");
            return true;
        } catch (IOException e) {
            log.error("Fail to delete file {}: {}", path, e);
            return false;
        }
    }

    @Override
    public boolean delete (String cid) {
        return delete (rootDir, cid);
    }

    @Override
    public InputStream get(String rootDir, String cid) {
        Path path = Paths.get("/", rootDir, cid);
        try {
            return new FileInputStream(path.toString());
        } catch (IOException e) {
            log.error("Fail to open file {}: {}", path, e);
            return null;
        }
    }

    @Override
    public InputStream get(String cid) {
        return get(rootDir, cid);
    }

    @Override
    public List<String> ls(String dir) {
        Path path = Paths.get("/", rootDir, dir);
        try {
            return Files.list(path)
                   .map(Path::toString)
                   .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Fail to list file {}: {}", path, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> stats() {
        return null;
    }

    @Override
    public String mkdir(String name) {
        return null;
    }
}
