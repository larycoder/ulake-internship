package org.usth.ict.ulake.ingest.crawler.fetcher;

import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;

public interface Fetcher<S, R> {
    void setup(Map<FetchConfig, String> config);
    void setup(Storage<S> store, Recorder<R> consumer);
    Map<String, Object> fetch(Policy query);
}
