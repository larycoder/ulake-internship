package org.usth.ict.ulake.ingest.crawler.fetcher;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;

import java.util.HashMap;
import java.util.List;

public interface Fetcher {
    void setup(HashMap<?, ?> config);
    void setup(Storage store, Recorder consumer);
    List<?> fetch();
}
