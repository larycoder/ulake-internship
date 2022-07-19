package org.usth.ict.ulake.ingest.crawler.storage.impl;

import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.StoreMacro;

public class EmptyStorageImpl implements Storage<Object> {

    @Override
    public void setup(Map<StoreMacro, String> config) {
        // TODO Empty
    }

    @Override
    public Map<String, String> store(Object data, Map<StoreMacro, String> meta) {
        // TODO Empty
        return null;
    }

    @Override
    public Object get(Map<StoreMacro, String> meta) {
        // TODO Empty
        return null;
    }
}
