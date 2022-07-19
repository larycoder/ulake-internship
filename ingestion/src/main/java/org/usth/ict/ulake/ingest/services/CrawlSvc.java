package org.usth.ict.ulake.ingest.services;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.impl.FetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.crawler.storage.impl.SqlLogStorageImpl;
import org.usth.ict.ulake.ingest.model.IngestLog;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.ProcessLog;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.ProcessLogRepo;

/**
 * Service to process crawl.
 * Aware Behavior: not thread-safe
 * */
public class CrawlSvc {
    private static final Logger log = LoggerFactory.getLogger(CrawlSvc.class);
    private DashboardService svc = null;
    private JsonWebToken jwt;
    private ProcessLogRepo processRepo;
    private FileLogRepo fileRepo;

    public class CrawlContext {
        public Policy policy;
        public FetchConfig mode;
        public Long processId;
        public Long folderId;
        public Storage<IngestLog> storeObj;
        public Recorder<InputStream> recordObj;
        public Fetcher<IngestLog, InputStream> fetchObj;
    }

    private CrawlContext context = new CrawlContext();

    public CrawlSvc(DashboardService svc, JsonWebToken jwt,
                    ProcessLogRepo processRepo, FileLogRepo fileRepo) {
        this.svc = svc;
        this.jwt = jwt;
        this.processRepo = processRepo;
        this.fileRepo = fileRepo;
    }

    private void buildStore() {
        context.storeObj = new SqlLogStorageImpl(processRepo, fileRepo);
    }

    private void buildRecord() {
        context.recordObj = new ULakeCacheFileRecorderImpl(svc);
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.FILE_PATH, "/tmp/ulake");
        recordConfig.put(Record.STORAGE_DIR, context.folderId.toString());
        recordConfig.put(Record.TOKEN, jwt.getRawToken());
        context.recordObj.setup(recordConfig);
    }

    private void buildFetch() {
        context.fetchObj = new FetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.MODE, context.mode.toString());
        fetchConfig.put(FetchConfig.PROCESS_ID, context.processId.toString());
        context.fetchObj.setup(fetchConfig);
        context.fetchObj.setup(context.storeObj, context.recordObj);
    }

    /**
     * Start crawl process.
     * Aware Behavior: file is stored to dir: "/tmp/ulake" before pushed to lake
     * */
    public Map<String, Object> runCrawl(
        Policy policy, FetchConfig mode, Long folderId, String desc) {

        context.policy = policy;
        context.mode = mode;
        context.folderId = folderId;

        log.info("Start crawl process...");
        if (mode == FetchConfig.DOWNLOAD) {
            var processLog = new ProcessLog();
            processLog.ownerId = Long.parseLong(jwt.getName());
            processLog.query = policy;
            processLog.folderId = folderId;
            processLog.description = desc;
            processLog.creationTime = new Date().getTime();
            processRepo.persist(processLog);
            context.processId = processLog.id;
        }

        log.info("Setup crawl components...");
        buildStore();
        buildRecord();
        buildFetch();

        log.info("Run crawl process");
        var resp = context.fetchObj.fetch(policy);

        log.info("Done crawl process...");
        if (mode == FetchConfig.DOWNLOAD) {
            var processLog = processRepo.findById(context.processId);
            processLog.endTime = new Date().getTime();
            processRepo.persist(processLog);
        }

        return resp;
    }
}
