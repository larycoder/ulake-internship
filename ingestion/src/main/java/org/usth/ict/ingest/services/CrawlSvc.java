package org.usth.ict.ingest.services;

import org.usth.ict.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ingest.crawler.fetcher.GithubFetcherImpl;
import org.usth.ict.ingest.crawler.recorder.Recorder;
import org.usth.ict.ingest.crawler.recorder.ulake.ULakeCacheFileRecorderImpl;
import org.usth.ict.ingest.models.DataModel;
import org.usth.ict.ingest.models.macro.FetchConfig;
import org.usth.ict.ingest.models.macro.Record;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CrawlSvc {
    private int fetchMode(String mode) {
       if(mode.equals("fetch")) {
           return FetchConfig.FETCH;
       } else if(mode.equals("download")) {
           return FetchConfig.DOWNLOAD;
       }
       return -1;
    }

    public DataModel runCrawl(Map policy, String mode) {
        Recorder recorder = new ULakeCacheFileRecorderImpl();
        String host = "http://core.ulake.sontg.net";
        String path = "/tmp/ulake";
        HashMap config = new HashMap();
        config.put(Record.HOST, host);
        config.put(Record.PATH, path);
        recorder.setup(config);

        Fetcher fetcher = new GithubFetcherImpl();
        config = new HashMap();
        config.put(FetchConfig.POLICY, policy);
        config.put(FetchConfig.MODE, fetchMode(mode));
        fetcher.setup(config);
        fetcher.setup(null, recorder);

        List data = fetcher.fetch();
        List head = (List) data.remove(0);
        return new DataModel(head, data);
    }
}
