package org.usth.ict.ulake.core.backend.impl;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return null;
    }

    @Override
    public String create(String rootDir, String name, long length, InputStream is) {
        return null;
    }

    @Override
    public boolean delete (String rootDir, String cid) {
        return false;
    }

    @Override
    public boolean delete (String cid) {
        return false;
    }

    @Override
    public InputStream get(String rootDir, String cid) {
        return null;
    }

    @Override
    public InputStream get(String cid) {
        return null;
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
