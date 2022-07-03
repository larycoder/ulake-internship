package org.usth.ict.ulake.ingest.crawler.fetcher;

import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;

public interface Fetcher<T, S> {
    void setup(Map<FetchConfig, String> config);
    void setup(Storage<S> store, Recorder<T> consumer);
    List<?> fetch();
}
