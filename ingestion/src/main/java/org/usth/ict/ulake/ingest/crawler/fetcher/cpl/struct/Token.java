package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct;

public class Token {
    public String type;
    public Object value;

    public Token(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Token(");
        builder.append(type + ", ");
        builder.append(value == null ? "null" : value.toString());
        builder.append(")");
        return builder.toString();
    }
}
