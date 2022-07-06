package org.usth.ict.ulake.extract.service;

import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;

public class ZipExtractor extends Extractor {

    public ZipExtractor(String token, CoreService coreService, FileService fileService) {
        super(token, coreService, fileService);
    }

    @Override
    public void extract(ExtractRequest request, ExtractResult result, ExtractCallback callback) {

    }
}
