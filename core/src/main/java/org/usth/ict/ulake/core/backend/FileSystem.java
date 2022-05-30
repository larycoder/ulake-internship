package org.usth.ict.ulake.core.backend;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileSystem {
    // general
    List<String> ls(String dir);
    Map<String, Integer> stats();

    // directories
    String mkdir(String name);

    // files
    String create(String name, long length, InputStream is);
    boolean delete(String cid);
    InputStream get(String cid);
}
