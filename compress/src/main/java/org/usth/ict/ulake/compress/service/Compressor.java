package org.usth.ict.ulake.compress.service;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.compress.model.CompressRequestFile;
import org.usth.ict.ulake.compress.model.CompressResult;

/**
 * General compressor interface
 */
public abstract class Compressor {
    protected static final Logger log = LoggerFactory.getLogger(ZipCompressor.class);

    @Inject
    @RestClient
    protected CoreService coreService;

    @Inject
    @RestClient
    protected FileService fileService;

    public Compressor() {
    }

    public abstract void compress(String bearer, List<CompressRequestFile> files, CompressResult result, CompressCallback callback);
}
