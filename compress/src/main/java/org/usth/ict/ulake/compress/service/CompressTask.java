package org.usth.ict.ulake.compress.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
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
    ZipCompressor compressor;

    @Inject
    @RestClient
    DashboardService dashboardService;

    public CompressTask() {
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CompressResult saveFirstResult(String bearer, Long id, Long userId, Long totalFiles) {
        CompressResult result = new CompressResult();
        result.requestId = id;
        result.ownerId = userId;
        result.totalFiles = totalFiles;
        repoResult.persist(result);
        log.info("Persisted first result {}", result.requestId);
        return result;
    }


    // dx: a bit ugly here... I know...
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void finishRequest(CompressRequest req) {
        // mark as finished in the request object
        CompressRequest persistReq = repoReq.findById(req.id);
        persistReq.finishedTime = new Date().getTime();
        repoReq.persist(persistReq);
    }

    @ActivateRequestContext
    @Transactional
    public void run(String bearer, Long id) {
        // prepare request files and result object
        var req = getRequest(id);
        var files = getFiles(id);
        var result = saveFirstResult(bearer, id, req.userId, Long.valueOf(files.size()));

        // go
        compressor.compress(bearer, files, result, this);
        push(bearer, result.url, req.folderId);

        finishRequest(req);
    }

    private CompressRequest getRequest(Long id) {
        return repoReq.findById(id);
    }

    private List<CompressRequestFile> getFiles(Long id) {
        return repoReqFile.list("requestId", id);
    }

    /**
     * Push a compressed file to server
     * @param bearer
     * @param fileName
     * @param folderId
     */
    private void push(String bearer, String fileName, Long folderId) {
        try {
            File f = new File(fileName);
            FolderModel folder = new FolderModel();
            folder.id = folderId;
            log.info("Push file to folder {}", folder.id);

            FileFormModel formModel = new FileFormModel();
            formModel.is = new FileInputStream(f);
            formModel.fileInfo = new FileModel();
            formModel.fileInfo.name = f.getName();
            formModel.fileInfo.mime = "application/zip";
            formModel.fileInfo.size = f.length();
            formModel.fileInfo.parent = folder;
            var file = dashboardService.newFile(bearer, formModel).getResp();
            formModel.is.close();
            log.info("- Successfully pushed zip file to dashboard, fileId={}", file.id);
        } catch (IOException e) {
            log.error("   + Cannot open zip file {}: {}", fileName, e.getMessage());
        } catch (LakeServiceException e) {
           log.error("   + Cannot push zip file {}: {}", fileName, e.getMessage());
        }
    }

    private boolean deleteLocalFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }


    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void callback(CompressRequestFile file, boolean success, CompressResult result) {
        log.info("  + callback: result id {}, file {}, status {}, progress {}", result.id, file.fileId, Boolean.valueOf(success), result.progress);
        if (success) {
            CompressResult persistResult = repoResult.findById(result.id);
            if (result.progress == null) result.progress = 0L;
            result.progress++;

            persistResult.progress = result.progress;
            repoResult.persist(persistResult);
            log.info("  + persisted result: id {}, progress {}/total {}", persistResult.id, persistResult.progress, persistResult.totalFiles);
        }
        // TODO: what to do when failed?
    }
}
