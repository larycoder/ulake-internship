package org.usth.ict.ulake.compress.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;

/**
 * General compressor interface
 */
public abstract class Compressor {
    protected static final Logger log = LoggerFactory.getLogger(ZipCompressor.class);

    protected String token;
    protected CoreService coreService;
    protected FileService folderService;

    public Compressor(String token, CoreService coreService, FileService folderService) {
        this.token = token;
        this.coreService = coreService;
        this.folderService = folderService;
    }

    public abstract void compress(List<RequestFile> files, Result result, CompressCallback callback);
}
