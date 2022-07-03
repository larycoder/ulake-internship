package org.usth.ict.ulake.ingest.services;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.GithubFetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.recorder.impl.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;

@ApplicationScoped
public class CrawlSvc {
    private FetchConfig fetchMode(String mode) {
        if (mode.equals("fetch")) {
            return FetchConfig.FETCH;
        } else if (mode.equals("download")) {
            return FetchConfig.DOWNLOAD;
        }
        return null;
    }

    public DataModel runCrawl(String policy, String mode) {
        Recorder<InputStream> recorder = new ULakeCacheFileRecorderImpl();
        String path = "/tmp/ulake";
        Map<Record, String> recordConfig = new HashMap<>();
        recordConfig.put(Record.PATH, path);
        recorder.setup(recordConfig);

        Fetcher<InputStream, String> fetcher = new GithubFetcherImpl();
        Map<FetchConfig, String> fetchConfig = new HashMap<>();
        fetchConfig.put(FetchConfig.POLICY, policy);
        fetchConfig.put(FetchConfig.MODE, fetchMode(mode).toString());
        fetcher.setup(fetchConfig);
        fetcher.setup(null, recorder);

        var data = fetcher.fetch();
        var head = (List<?>) data.remove(0);
        return new DataModel(head, data);
    }
}
