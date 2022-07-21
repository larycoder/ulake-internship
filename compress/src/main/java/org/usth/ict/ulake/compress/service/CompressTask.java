package org.usth.ict.ulake.compress.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.core.ObjectFormModel;
import org.usth.ict.ulake.common.model.core.ObjectModel;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
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
        push(bearer, result.url, req.folderId);

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

    /**
     * Push a compressed file to server
     * @param bearer
     * @param fileName
     * @param folderId
     */
    private void push(String bearer, String fileName, Long folderId) {
        try {
            File f = new File(fileName);
            FileFormModel formModel = new FileFormModel();
            formModel.is = new FileInputStream(f);
            formModel.fileInfo = new FileModel();
            formModel.fileInfo.name = f.getName();
            formModel.fileInfo.mime = "application/zip";
            formModel.fileInfo.size = f.length();
            var file = dashboardService.newFile(bearer, formModel).getResp();
            formModel.is.close();
            log.info("- Successfully pushed zip file to dashboard, fileId={}", file.id);
        } catch (IOException e) {
            log.error("   + Cannot open zip file {}: {}", result.url, e.getMessage());
        } catch (LakeServiceException e) {
           log.error("   + Cannot push zip file {}: {}", result.url, e.getMessage());
        }
    }

    private boolean deleteLocalFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
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
