package org.usth.ict.ulake.core.backend;

import com.google.protobuf.ByteString;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileSystem {
    // general
    List<String> ls(String dir);
    Map<String, Object> stats();

    // directories
    String mkdir(String name);

    // files
    String create(String rootDir, String name, long length, InputStream is);
    String create(String name, long length, InputStream is);

    String create(String name, long length, ByteString is);
    String create(String rootDir, String name, long length, ByteString is);

    //    append to file with given cid
    boolean insertChunk(String cid, ByteString is);

    boolean delete(String rootDir, String cid);
    boolean delete(String cid);
    InputStream get(String rootDir, String cid);
    InputStream get(String cid);
}
