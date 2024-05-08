package org.usth.ict.ulake.dashboard.service;

import jakarta.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// A new instance of ExtractJob is created by Quartz for every job execution
public class IrFeatureExtractJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(IrFeatureExtractJob.class);

    @Inject
    IrFeatureExtractTask task;

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