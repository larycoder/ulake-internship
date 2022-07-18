package org.usth.ict.ulake.extract.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.persistence.ExtractRequestRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultFileRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultRepository;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ExtractorBean {
    private static final Logger log = LoggerFactory.getLogger(ExtractorBean.class);

    @Inject
    org.quartz.Scheduler quartz;

    public String token;

    @Inject
    ExtractRequestRepository repoReq;

    @Inject
    ExtractResultFileRepository repoResFile;

    @Inject
    ExtractResultRepository repoRes;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    @RestClient
    FileService fileService;

    @Inject
    @RestClient
    DashboardService dashboardService;

    @Inject
    ZipExtractor extractor;

    @Inject
    ExtractTask task;

    /**
     * Start extraction service in background with specified id
     * @param id Compression request Id
     */
    @Transactional
    public void extract() {
        log.info("Start extraction in managed bean");
        log.info("req count: {}", repoReq.count());
        log.info("req result count: {}", repoRes.count());
        log.info("req file result count: {}", repoResFile.count());
        //task.requestId = id;
        //task.run();
    }

    //void onStart(@Observes StartupEvent event) throws SchedulerException {
    public void scheduleNow() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(MyJob.class)
                          .withIdentity("myJob", "myGroup")
                          .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                             .withIdentity("myTrigger", "myGroup")
                             .startNow()
                             .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(1)
                                   )
                             .build();
        quartz.scheduleJob(job, trigger);
     }

     // A new instance of MyJob is created by Quartz for every job execution
     public static class MyJob implements Job {

        @Inject
        ExtractorBean taskBean;

        public void execute(JobExecutionContext context) throws JobExecutionException {
           taskBean.extract();
        }

     }
}
