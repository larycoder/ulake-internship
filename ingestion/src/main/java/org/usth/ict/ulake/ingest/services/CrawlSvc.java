package org.usth.ict.ulake.ingest.services;

import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.GithubFetcherImpl;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.recorder.ulake.ULakeCacheFileRecorderImpl;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CrawlSvc {
    private FetchConfig fetchMode(String mode) {
       if(mode.equals("fetch")) {
           return FetchConfig.FETCH;
       } else if(mode.equals("download")) {
           return FetchConfig.DOWNLOAD;
       }
       return null;
    }

    public DataModel runCrawl(Map<String, Object> policy, String mode) {
        Recorder recorder = new ULakeCacheFileRecorderImpl();
        String host = "http://core.ulake.sontg.net";
        String path = "/tmp/ulake";
        HashMap<Object, Object> config = new HashMap<>();
        config.put(Record.HOST, host);
        config.put(Record.PATH, path);
        recorder.setup(config);

        Fetcher fetcher = new GithubFetcherImpl();
        config = new HashMap<>();
        config.put(FetchConfig.POLICY, policy);
        config.put(FetchConfig.MODE, fetchMode(mode));
        fetcher.setup(config);
        fetcher.setup(null, recorder);

        var data = fetcher.fetch();
        var head = (List<?>) data.remove(0);
        return new DataModel(head, data);
    }
}
