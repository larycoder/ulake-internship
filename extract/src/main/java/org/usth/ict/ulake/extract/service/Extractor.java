package org.usth.ict.ulake.extract.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;

/**
 * General extractor interface
 */
public abstract class Extractor {
    protected static final Logger log = LoggerFactory.getLogger(Extractor.class);

    protected String token;
    protected CoreService coreService;
    protected FileService fileService;

    public Extractor(String token, CoreService coreService, FileService fileService) {
        this.token = token;
        this.coreService = coreService;
        this.fileService = fileService;
    }

    public abstract void extract(ExtractRequest request, ExtractResult result, ExtractCallback callback);
}
