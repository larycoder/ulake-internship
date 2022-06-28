package org.usth.ict.ingest.resources.example;

import org.usth.ict.ingest.crawler.fetcher.GithubFetcherImpl;
import org.usth.ict.ingest.crawler.recorder.FileRecorderImpl;
import org.usth.ict.ingest.crawler.recorder.Recorder;
import org.usth.ict.ingest.models.macro.FetchConfig;
import org.usth.ict.ingest.models.macro.Record;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/exec")
public class ExecExample {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List exec(Map data) {
        // recorder
        Recorder recorder = new FileRecorderImpl();

        String path = System.getProperty("user.dir");
        path += "/resources/data/github";
        //String path = "http://core.ulake.sontg.net";

        HashMap config = new HashMap();
        config.put(Record.PATH, path);
        recorder.setup(config);

        // fetcher
        GithubFetcherImpl fetcher = new GithubFetcherImpl();
        config = new HashMap();
        config.put(FetchConfig.POLICY, data);
        config.put(FetchConfig.MODE, FetchConfig.DOWNLOAD);
        fetcher.setup(config);
        fetcher.setup(null, recorder);

        return fetcher.fetch();
    }
}
