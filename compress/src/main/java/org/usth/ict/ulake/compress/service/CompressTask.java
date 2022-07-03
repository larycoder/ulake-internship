package org.usth.ict.ulake.compress.service;

import java.util.Date;
import java.util.List;

import org.usth.ict.ulake.compress.model.Request;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;
import org.usth.ict.ulake.compress.persistence.RequestFileRepository;
import org.usth.ict.ulake.compress.persistence.RequestRepository;
import org.usth.ict.ulake.compress.persistence.ResultRepository;

/**
 * Perform compression in a background thread
 */
public class CompressTask implements CompressCallback {

    private Long requestId;
    private RequestRepository repoReq;
    private RequestFileRepository repoReqFile;
    private ResultRepository repoResult;
    private Compressor compressor;

    private Result result;

    public CompressTask(Compressor compressor, Long requestId, String token, RequestRepository repoReq, RequestFileRepository repoReqFile, ResultRepository repoResult) {
        this.compressor = compressor;
        this.requestId = requestId;
        this.repoReq = repoReq;
        this.repoReqFile = repoReqFile;
        this.repoResult = repoResult;
    }

    public void run() {
        // prepare request files and result object
        var req = getRequest();
        var files = getFiles();
        result = new Result();
        result.requestId = requestId;
        result.ownerId = req.userId;
        result.totalFiles = (long) files.size();
        repoResult.persist(result);

        // go
        compressor.compress(files, this);

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
    }

    private Request getRequest() {
        return repoReq.findById(requestId);
    }

    private List<RequestFile> getFiles() {
        return repoReqFile.list("requestId", requestId);
    }

    @Override
    public void callback(RequestFile file) {
        result.totalFiles++;
        repoResult.persist(result);
    }
}
