package org.usth.ict.ulake.core.backend;

import java.io.InputStream;

public interface FileSystem {
    String create(InputStream is);
    boolean delete(String cid);
    InputStream get(String cid);
}
