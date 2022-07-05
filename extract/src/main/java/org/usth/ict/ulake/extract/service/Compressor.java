package org.usth.ict.ulake.extract.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractResultFile;
import org.usth.ict.ulake.extract.model.ExtractResult;

/**
 * General compressor interface
 */
public abstract class Compressor {
    protected static final Logger log = LoggerFactory.getLogger(ZipCompressor.class);

    protected String token;
    protected CoreService coreService;
    protected FileService fileService;

    public Compressor(String token, CoreService coreService, FileService fileService) {
        this.token = token;
        this.coreService = coreService;
        this.fileService = fileService;
    }

    public abstract void compress(List<ExtractResultFile> files, ExtractResult result, CompressCallback callback);
}
