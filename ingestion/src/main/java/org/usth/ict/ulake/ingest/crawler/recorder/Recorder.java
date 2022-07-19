package org.usth.ict.ulake.ingest.crawler.recorder;

import java.util.Map;

import org.usth.ict.ulake.ingest.model.macro.Record;

public interface Recorder<T> {
    void setup(Map<Record, String> config);
    void record(T data, Map<Record, String> meta);
    Map<String, String> info();
}
