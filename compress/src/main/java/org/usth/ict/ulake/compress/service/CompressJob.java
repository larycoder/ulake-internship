package org.usth.ict.ulake.compress.service;

import jakarta.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// A new instance of ExtractJob is created by Quartz for every job execution
public class CompressJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(CompressJob.class);

    @Inject
    CompressTask task;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Integer id = (Integer) context.getJobDetail().getJobDataMap().get("id");
        String bearer = (String) context.getJobDetail().getJobDataMap().get("bearer");
        if (id == null || bearer == null) {
            log.error("Cannot retrieve job details for compress Id");
            return;
        }
        log.info("Start compress job for id {}", id);
        task.run(bearer, Long.valueOf(id));
    }
}