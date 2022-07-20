package org.usth.ict.ulake.compress.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.task.ScheduledTask;
import org.usth.ict.ulake.compress.model.CompressRequest;
import org.usth.ict.ulake.compress.model.CompressRequestFile;
import org.usth.ict.ulake.compress.model.CompressResult;
import org.usth.ict.ulake.compress.persistence.RequestFileRepository;
import org.usth.ict.ulake.compress.persistence.RequestRepository;
import org.usth.ict.ulake.compress.persistence.ResultRepository;

/**
 * Perform compression in a background thread
 */
@ApplicationScoped
public class CompressTask extends ScheduledTask implements CompressCallback {
    private static final Logger log = LoggerFactory.getLogger(CompressTask.class);

    @Inject
    RequestRepository repoReq;

    @Inject
    RequestFileRepository repoReqFile;

    @Inject
    ResultRepository repoResult;

    @Inject
    CoreService coreService;

    @Inject
    ZipCompressor compressor;

    private CompressResult result;

    public CompressTask() {
    }

    @Transactional
    public void run(String bearer, Long id) {
        // prepare request files and result object
        var req = getRequest(id);
        var files = getFiles(id);
        result = new CompressResult();
        result.requestId = id;
        result.ownerId = req.userId;
        result.totalFiles = (long) files.size();
        repoResult.persist(result);

        // go
        compressor.compress(bearer, files, result, this);

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
        repoReq.persist(req);
    }

    private CompressRequest getRequest(Long id) {
        return repoReq.findById(id);
    }

    private List<CompressRequestFile> getFiles(Long id) {
        return repoReqFile.list("requestId", id);
    }

    @Override
    public void callback(CompressRequestFile file, boolean success, CompressResult result) {
        log.info("  + callback: file {}, status {}, progress {}", file.fileId, Boolean.valueOf(success), result.progress);
        if (success) {
            if (result.progress == null) result.progress = 0L;
            result.progress++;
            repoResult.persist(result);
            log.info("  + persisted result: id {}, progress {}/total {}", result.id, result.progress, result.totalFiles);
        }
        // TODO: what to do when failed?
    }
}
