package org.usth.ict.ulake.ingest.crawler.storage;

import java.util.Map;

import org.usth.ict.ulake.ingest.model.macro.StoreMacro;

public interface Storage<T> {
    void setup(Map<StoreMacro, String> config);
    Map<String, String> store(T data, Map<StoreMacro, String> meta);
    T get(Map<StoreMacro, String> meta);
}
