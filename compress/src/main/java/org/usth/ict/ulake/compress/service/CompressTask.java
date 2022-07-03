package org.usth.ict.ulake.compress.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
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
    private static final Logger log = LoggerFactory.getLogger(CompressTask.class);

    private Long requestId;
    private RequestRepository repoReq;
    private RequestFileRepository repoReqFile;
    private ResultRepository repoResult;
    private Compressor compressor;

    private Result result;

    public CompressTask(Compressor compressor, Long requestId, RequestRepository repoReq, RequestFileRepository repoReqFile, ResultRepository repoResult) {
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
        compressor.compress(files, result, this);
        String localFilePath = pushCore(result);
        deleteLocalFile(localFilePath);

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
        repoReq.persist(req);
    }

    private Request getRequest() {
        return repoReq.findById(requestId);
    }

    private List<RequestFile> getFiles() {
        return repoReqFile.list("requestId", requestId);
    }

    /**
     * push the zipped file to core temp repository
     * @param result
     * @return local file name
     */
    private String pushCore(Result result) {
        String ret = result.url;
        try {
            log.info(" + Going to push temporary file to core, cid={}", result.url);
            FileInputStream fis = new FileInputStream(new File(result.url));
            LakeHttpResponse resp = compressor.coreService.newTemp(compressor.token, fis);
            if (resp.getCode() != 200) {
                return null;
            }
            result.url = resp.getMsg();
            fis.close();
            log.info(" + Successfully pushed temporary file to core, cid={}", result.url);
        } catch (IOException e) {
            log.error("   + Cannot open zip file {}: {}", result.url, e.getMessage());
            return null;
        }
        return ret;
    }

    private boolean deleteLocalFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    @Override
    public void callback(RequestFile file, boolean success, Result result) {
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
