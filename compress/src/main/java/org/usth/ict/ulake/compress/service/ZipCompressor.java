package org.usth.ict.ulake.compress.service;

import java.util.List;

import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;

public class ZipCompressor extends Compressor {

    public ZipCompressor(String token, CoreService coreService, FileService folderService) {
        super(token, coreService, folderService);
    }

    @Override
    public void compress(List<RequestFile> files, Result result, CompressCallback callback) {
        for (var file: files) {
            log.info("- Zip compressing {}", file.fileId);

            callback.callback(file, result);
        }
        log.info("- Zip compression finished {} files", files.size());
    }
}
