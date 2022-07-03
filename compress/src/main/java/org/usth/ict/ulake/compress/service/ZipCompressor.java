package org.usth.ict.ulake.compress.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;

public class ZipCompressor implements Compressor {
    private static final Logger log = LoggerFactory.getLogger(ZipCompressor.class);

    @Override
    public void compress(List<RequestFile> files, Result result, CompressCallback callback) {
        for (var file: files) {
            log.info("- Zip compressed {}", file.fileId);
            callback.callback(file, result);
        }
        log.info("- Zip compression finished {} files", files.size());
    }
}
