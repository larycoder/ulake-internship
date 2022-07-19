package org.usth.ict.ulake.extract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;
import org.usth.ict.ulake.extract.model.ExtractResultFile;
import org.usth.ict.ulake.extract.persistence.ExtractRequestRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultFileRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultRepository;

/**
 * Perform extraction in a background thread
 */
@ApplicationScoped
public class ExtractTask implements ExtractCallback {
    private static final Logger log = LoggerFactory.getLogger(ExtractTask.class);

    Long requestId;

    @Inject
    Scheduler quartz;

    @Inject
    ExtractRequestRepository repoReq;

    @Inject
    ExtractResultFileRepository repoResultFile;

    @Inject
    ExtractResultRepository repoResult;

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
    public void run(Long id) {
        this.requestId = id;
        // prepare request files and result object
        var req = getRequest();
        log.info("Going into the request {}...", req);


        result = new ExtractResult();
        result.requestId = requestId;
        result.ownerId = req.userId;
        result.progress = 0L;
        log.info("Before persisting result");
        repoResult.persist(result);

        log.info("After persisting result");

        // go
        extractor.extract(req, result, this);
        log.info("After extracting result");
        repoResult.persist(result);

        // push all extracted files to dashboard
        log.info("Before listing extracting result");
        List<ExtractResultFile> resultFiles = repoResultFile.list("requestId", this.requestId);
        log.info("After listing extracting result");
        for (var file: resultFiles) {
            log.info("Before pushing local file to server");
            String localFilePath = pushFile(file);
            log.info("Before deleting local file");
            deleteLocalFile(localFilePath);
        }

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
        log.info("Before persisting finishedTime for completion");
        repoReq.persist(req);
        log.info("After persisting finishedTime for completion");
    }

    private ExtractRequest getRequest() {
        return repoReq.findById(requestId);
    }

    /**
     * Push the extracted file to file/folder repository
     * Using dashboard service
     * @param result
     * @return local file name
     */
    private String pushFile(ExtractResultFile resultFile) {
        String ret = result.url;
        try {
            FileInputStream fis = new FileInputStream(new File(result.url));
            LakeHttpResponse resp = extractor.coreService.newTemp(extractor.token, fis);
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
    public void start(Long id) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
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
            if (id == null) {
                log.error("Cannot retrieve job details for extract Id");
                return;
            }
            log.info("id {}", id);
            task.run(Long.valueOf(id));
        }

    }
}
