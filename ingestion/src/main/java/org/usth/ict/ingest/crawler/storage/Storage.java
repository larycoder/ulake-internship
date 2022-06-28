package org.usth.ict.ingest.crawler.storage;

import java.util.HashMap;

public interface Storage {
    void setup(HashMap config);
    void store(Object data, HashMap meta);
    void get(Object carrier, HashMap meta);
}
