package org.usth.ict.ulake.ingest.crawler.fetcher.impl;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;

public class FetcherImpl implements Fetcher<String, InputStream> {
    private static final Logger log = LoggerFactory.getLogger(FetcherImpl.class);
    private FetchConfig mode;

    @Override
    public void setup(Map<FetchConfig, String> config) {
        mode = FetchConfig.valueOf(config.get(FetchConfig.MODE));
    }

    @Override
    public void setup(Storage<String> store, Recorder<InputStream, String> consumer) {}

    @Override
    public Map<String, Object> fetch(Policy policy) {
        Interpreter engine = new Interpreter();
        var resultTable = engine.eval(policy);

        if (mode == FetchConfig.FETCH) {
            return resultTable.extractAsMap();
        } else if (mode == FetchConfig.DOWNLOAD) {
            // TODO: download and upload file to lake
            return null;
        } else {
            return null;
        }
    }
}
