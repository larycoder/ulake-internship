package org.usth.ict.ulake.ingest.model;

import java.util.List;
import java.util.Map;

public class PolicyModel {
    public abstract class Data {
        public class Pattern {
            public String value;
            public Map<String, String> var;
        }

        public class Request {
            public String method;
            public Pattern path;
            public Pattern head;
            public Pattern body;
        }

        public Request req;
        public Pattern pattern;
        public String map;
    }

    public Map<String, List<String>> declare;
    public Map<String, Data> pipe;
    public String pReturn;
}
