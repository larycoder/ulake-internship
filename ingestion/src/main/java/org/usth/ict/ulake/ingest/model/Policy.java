package org.usth.ict.ulake.ingest.model;

import java.util.List;
import java.util.Map;

public class Policy {
    public Map<String, List<String>> declare;
    public Map<String, PolicyData> pipe;
    public String pReturn;
}
