package org.usth.ict.ulake.ingest.services;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.crawler.fetcher.impl.FetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;

@ApplicationScoped
public class CrawlSvc {
    private static final Logger log = LoggerFactory.getLogger(CrawlSvc.class);

    @Inject
    @RestClient
    DashboardService svc;

    public Map<String, Object> runCrawl(
        Policy policy, FetchConfig mode, Long folderId, String token) {
        log.info("Setup recorder...");
        var recorder = new ULakeCacheFileRecorderImpl(svc);
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.FILE_PATH, "/tmp/ulake");
        recordConfig.put(Record.STORAGE_DIR, folderId.toString());
        recordConfig.put(Record.TOKEN, token);
        recorder.setup(recordConfig);

        log.info("Setup fetcher...");
        var fetcher = new FetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.MODE, mode.toString());
        fetcher.setup(fetchConfig);
        fetcher.setup(null, recorder);

        log.info("Run fetcher...");
        return fetcher.fetch(policy);
    }
}
