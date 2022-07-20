package org.usth.ict.ulake.extract.service;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.task.ScheduledTask;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;
import org.usth.ict.ulake.extract.model.ExtractResultFile;
import org.usth.ict.ulake.extract.persistence.ExtractRequestRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultFileRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Perform extraction in a background thread
 */
@ApplicationScoped
public class ExtractTask extends ScheduledTask implements ExtractCallback {
    private static final Logger log = LoggerFactory.getLogger(ExtractTask.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    ExtractRequestRepository repoReq;

    @Inject
    ExtractResultFileRepository repoResultFile;

    @Inject
    ExtractResultRepository repoResult;

    @Inject
    @RestClient
    protected DashboardService dashboardService;

    @Inject
    @RestClient
    protected CoreService coreService;

    @Inject
    ZipExtractor extractor;

    private ExtractResult result;

    public ExtractTask() {
    }

    /**
     * Performs the extract task in current thread
     * @param id
     */
    @Transactional
    public void run(String bearer, Long id) {
        // prepare request files and result object
        var req = getRequest(id);

        // make a new folder for the request
        Long destFolderId = 0L;
        try {
            FileModel file = dashboardService.fileInfo(req.fileId, bearer).getResp();

            String extractDirName = file.name;
            if (extractDirName.contains(".")) {
                extractDirName = extractDirName.substring(0, extractDirName.lastIndexOf("."));
            }
            else {
                extractDirName += "-extracted";
            }
            destFolderId = this.mkdir(bearer, extractDirName);
            log.info("Created folder id {}, name {} as destination folder", destFolderId, extractDirName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.folderId = destFolderId;
        repoReq.persist(req);

        result = new ExtractResult();
        result.requestId = id;
        result.ownerId = req.userId;
        result.progress = 0L;
        repoResult.persist(result);

        // go
        extractor.extract(bearer, req, result, this);
        repoResult.persist(result);

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
        repoReq.persist(req);
        log.info("Extraction job {} finished", req.id);
    }

    private ExtractRequest getRequest(Long id) {
        return repoReq.findById(id);
    }

    /**
     * Create a new folder for the destination
     */
    private Long mkdir(String bearer, String name) {
        try {
            FolderModel folder = new FolderModel();
            folder.name = name;
            var createdFolder = dashboardService.newFolder(bearer, folder).getResp();
            if (createdFolder != null)
                return createdFolder.id;
            else
                return 0L;
        } catch (LakeServiceException e) {
            return 0L;
        }
    }

    @Override
    @Transactional
    public void callback(ExtractResultFile file, boolean success, ExtractResult result) {
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
