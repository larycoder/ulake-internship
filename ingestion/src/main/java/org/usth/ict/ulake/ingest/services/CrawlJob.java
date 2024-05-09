package org.usth.ict.ulake.ingest.services;

import jakarta.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(CrawlJob.class);

    @Inject
    CrawlTask task;

    @Override
    public void execute(JobExecutionContext context)
    throws JobExecutionException {
        var id = (Integer) context.getJobDetail().getJobDataMap().get("id");
        var tok = (String) context.getJobDetail().getJobDataMap().get("bearer");
        if (id == null || tok == null) {
            log.error("Cannot retrieve job details for crawl process Id");
            return;
        }
        log.info("Start crawl job for id {}", id);
        task.runCrawl(tok, Long.valueOf(id));
    }
}
