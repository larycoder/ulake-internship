package org.usth.ict.ulake.ingest.services;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.impl.FetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;

@ApplicationScoped
public class CrawlSvc {
    private static final Logger log = LoggerFactory.getLogger(CrawlSvc.class);

    private FetchConfig fetchMode(String mode) {
        if (mode.equals("fetch")) {
            return FetchConfig.FETCH;
        } else if (mode.equals("download")) {
            return FetchConfig.DOWNLOAD;
        }
        return null;
    }

    public Map<String, Object> runCrawl(Policy policy, String mode) {
        log.info("Setup recorder...");
        Recorder<InputStream> recorder = new ULakeCacheFileRecorderImpl();
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.PATH, "/tmp/ulake");
        recorder.setup(recordConfig);

        log.info("Setup fetcher...");
        Fetcher<String, InputStream> fetcher = new FetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.MODE, fetchMode(mode).toString());
        fetcher.setup(fetchConfig);

        log.info("Run fetcher...");
        return fetcher.fetch(policy);
    }
}
