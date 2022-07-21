package org.usth.ict.ulake.ingest.services;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.task.ScheduledTask;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.impl.FetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.crawler.storage.impl.SqlLogStorageImpl;
import org.usth.ict.ulake.ingest.model.IngestLog;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.ProcessLogRepo;

/**
 * Service to process crawl.
 * */
@ApplicationScoped
public class CrawlTask extends ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(CrawlTask.class);

    @Inject
    @RestClient
    DashboardService svc;

    @Inject
    JsonWebToken jwt;

    @Inject
    ProcessLogRepo processRepo;

    @Inject
    FileLogRepo fileRepo;

    public class CrawlContext {
        public Policy policy;
        public FetchConfig mode;
        public Long processId;
        public Long folderId;
        public String token;
        public Storage<IngestLog> storeObj;
        public Recorder<InputStream> recordObj;
        public Fetcher<IngestLog, InputStream> fetchObj;
    }

    private void buildStore(CrawlContext context) {
        context.storeObj = new SqlLogStorageImpl(processRepo, fileRepo);
    }

    /**
     * Create recorder object.
     * Aware Behavior: file is stored to dir: "/tmp/ulake" before pushed to lake
     * */
    private void buildRecord(CrawlContext context) {
        context.recordObj = new ULakeCacheFileRecorderImpl(svc);
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.FILE_PATH, "/tmp/ulake");
        recordConfig.put(Record.STORAGE_DIR, context.folderId.toString());
        recordConfig.put(Record.TOKEN, context.token);
        context.recordObj.setup(recordConfig);
    }

    private void buildFetch(CrawlContext context) {
        context.fetchObj = new FetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.MODE, context.mode.toString());
        if (context.processId != null)
            fetchConfig.put(FetchConfig.PROCESS_ID,
                            context.processId.toString());
        context.fetchObj.setup(fetchConfig);
        context.fetchObj.setup(context.storeObj, context.recordObj);
    }

    public Map<String, Object> runFetch(Policy policy) {
        var context = new CrawlContext();
        context.policy = policy;
        context.mode = FetchConfig.FETCH;

        log.info("Setup fetch components...");
        buildFetch(context);

        log.info("Run fetch process");
        return context.fetchObj.fetch(policy);
    }

    /**
     * Start crawl process.
     * */
    @Transactional
    public void runCrawl(String bearer, Long processId) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var context = new CrawlContext();
        var processLog = processRepo.findById(processId);

        context.policy = processLog.query;
        context.mode = FetchConfig.DOWNLOAD;
        context.processId = processId;
        context.folderId = processLog.folderId;
        context.token = bearer;

        log.info("Setup crawl components...");
        buildStore(context);
        buildRecord(context);
        buildFetch(context);

        log.info("Run crawl process");
        context.fetchObj.fetch(context.policy);

        log.info("Done crawl process, record to log...");
        processLog.endTime = new Date().getTime();
        processRepo.persist(processLog);
    }
}
