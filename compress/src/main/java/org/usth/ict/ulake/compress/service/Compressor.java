package org.usth.ict.ulake.compress.service;

import java.util.List;

import org.usth.ict.ulake.compress.model.RequestFile;

/**
 * General compressor interface
 */
public interface Compressor {
    public void compress(List<RequestFile> files, CompressCallback callback);
}
