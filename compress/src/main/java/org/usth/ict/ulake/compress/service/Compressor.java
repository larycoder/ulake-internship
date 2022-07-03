package org.usth.ict.ulake.compress.service;

import java.util.List;

import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;

/**
 * General compressor interface
 */
public interface Compressor {
    public void compress(List<RequestFile> files, Result result, CompressCallback callback);
}
