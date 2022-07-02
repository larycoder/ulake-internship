package org.usth.ict.ulake.ingest.model;

import java.util.List;
import java.util.Map;

public class PolicyModel {
    public abstract class Data {
        public class Request {

            public class Path {
                public String value;
                public Map<String, String> var;
            }

            public String method;
            public Path path;
        }

        public class Pattern {
            public String value;
        }

        public Request req;
        public Pattern pattern;
        public String map;
    }
    public Map<String, List<String>> declare;
    public Map<String, Data> pipe;
    public String pReturn;
}
