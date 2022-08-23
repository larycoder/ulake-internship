package org.usth.ict.ulake.lcc.service;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LccJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(LccJob.class);

    @Inject
    LccTask task;

    @Override
    public void execute(JobExecutionContext context)
    throws JobExecutionException {
        var id = (Integer) context.getJobDetail().getJobDataMap().get("id");
        var tok = (String) context.getJobDetail().getJobDataMap().get("bearer");
        if (id == null || tok == null) {
            log.error("Cannot retrieve job details for process lcc patient id");
            return;
        }
        log.info("Start crawl job for id {}", id);
        task.run(tok, Long.valueOf(id));
    }

}
