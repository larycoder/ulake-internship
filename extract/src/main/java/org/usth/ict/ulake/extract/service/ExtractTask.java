package org.usth.ict.ulake.extract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
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
public class ExtractTask implements ExtractCallback {
    private static final Logger log = LoggerFactory.getLogger(ExtractTask.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    Scheduler quartz;

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

        // push all extracted files to dashboard
        List<ExtractResultFile> resultFiles = repoResultFile.list("requestId", id);
        for (var file: resultFiles) {
            String localFilePath = pushFile(bearer, file);
            deleteLocalFile(localFilePath);
        }

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

    /**
     * Push the extracted file to file/folder repository
     * Using dashboard service
     * @param result
     * @return local file name
     */
    private String pushFile(String bearer, ExtractResultFile resultFile) {
        String ret = result.url;
        try {
            FileInputStream fis = new FileInputStream(new File(result.url));
            LakeHttpResponse resp = extractor.coreService.newTemp(bearer, fis);
            if (resp.getCode() != 200) {
                return null;
            }
            result.url = resp.getResp().toString();
            fis.close();
            log.info(" + Successfully pushed temporary file to core, cid={}", result.url);
        } catch (IOException e) {
            log.info("   + Cannot open zip file {} for pushing: {}", result.url, e.getMessage());
            return null;
        }
        return ret;
    }

    private boolean deleteLocalFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
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

    /**
     * Schedules and starts the extract task in a background thread, managed by quartz
     * @throws SchedulerException
     */
    public void start(String bearer, Long id) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("bearer", bearer);
        jobDataMap.put("id", id.intValue());
        JobDetail job = JobBuilder.newJob(ExtractJob.class)
                .withIdentity("extract", "extract-job")
                .setJobData(jobDataMap)
                .build();
        var scheduler = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(1);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "extract-trigger")
                .startNow()
                .withSchedule(scheduler)
                .build();
        quartz.scheduleJob(job, trigger);
    }

    // A new instance of ExtractJob is created by Quartz for every job execution
    public static class ExtractJob implements Job {
        @Inject
        ExtractTask task;

        public void execute(JobExecutionContext context) throws JobExecutionException {
            Integer id = (Integer) context.getJobDetail().getJobDataMap().get("id");
            String bearer = (String) context.getJobDetail().getJobDataMap().get("bearer");
            if (id == null || bearer == null) {
                log.error("Cannot retrieve job details for extract Id");
                return;
            }
            log.info("Start extract job for id {}", id);
            task.run(bearer, Long.valueOf(id));
        }
    }
}
