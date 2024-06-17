package org.usth.ict.ulake.common.task;

import java.util.UUID;

import jakarta.inject.Inject;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Inject
    Scheduler quartz;

    /**
     * Schedules and starts the extract task in a background thread, managed by quartz
     * @throws SchedulerException
     */
    public void start(String bearer, Long id, Class<? extends Job> jobClass) throws SchedulerException {
        String identity = UUID.randomUUID().toString();
        log.info("Starting a new job {}", jobClass.getSimpleName());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("bearer", bearer);
        jobDataMap.put("id", id.intValue());
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(identity + "-job-key", identity + "-job-value")
                .setJobData(jobDataMap)
                .build();
        var scheduler = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(1);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(identity + "-trigger-key", identity + "-trigger-value")
                .startNow()
                .withSchedule(scheduler)
                .build();
        quartz.scheduleJob(job, trigger);
    }
}
