package org.usth.ict.ulake.core.backend;

import java.io.InputStream;
import java.util.List;

public interface FileSystem {
    // general
    List<String> ls(String dir);

    // directories
    String mkdir(String name);

    // files
    String create(InputStream is);
    boolean delete(String cid);
    InputStream get(String cid);
}
