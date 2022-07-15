package org.usth.ict.ulake.ingest.services;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.ingest.crawler.fetcher.impl.FetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;

@ApplicationScoped
public class CrawlSvc {
    private static final Logger log = LoggerFactory.getLogger(CrawlSvc.class);

    public Map<String, Object> runCrawl(Policy policy, FetchConfig mode) {
        log.info("Setup recorder...");
        var recorder = new ULakeCacheFileRecorderImpl();
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.PATH, "/tmp/ulake");
        recorder.setup(recordConfig);

        log.info("Setup fetcher...");
        var fetcher = new FetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.MODE, mode.toString());
        fetcher.setup(fetchConfig);

        log.info("Run fetcher...");
        return fetcher.fetch(policy);
    }
}
