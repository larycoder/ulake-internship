package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;

public class DataNode extends ASTNode {
    public Object value;

    public DataNode(
            Token token, ASTNode left, ASTNode right) {
        super(token, left, right);
        this.node = DATA;
        this.value = token.value;
    }

    public DataNode(Token token) {
        this(token, null, null);
    }
}
