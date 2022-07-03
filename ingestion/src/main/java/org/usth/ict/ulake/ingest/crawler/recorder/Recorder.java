package org.usth.ict.ulake.ingest.crawler.recorder;

import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.Record;

public interface Recorder<T> {
    void setup(Map<Record, String> config);
    void setup(Storage store);
    void record(T data, Map<Record, String> meta);
    void info(Map<String, String> carrier, Map<Record, String> meta);
}
