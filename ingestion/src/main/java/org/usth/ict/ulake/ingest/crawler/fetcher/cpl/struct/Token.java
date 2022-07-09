package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
    public Type type;
    public Map.Entry<String, List<String>> mapValue;
    public String stringValue;

    public Token(Type type, Map.Entry<String, List<String>> value) {
        this.type = type;
        this.mapValue = value;
    }

    public Token(Type type, String value) {
        this.type = type;
        this.stringValue = value;
    }

    public String toString() {
        Map<String, Object> expr = new HashMap<>();
        expr.put("type", type);
        expr.put("map_value", mapValue);
        expr.put("string_value", stringValue);
        return "Token(" + expr.toString() + ")";
    }
}
