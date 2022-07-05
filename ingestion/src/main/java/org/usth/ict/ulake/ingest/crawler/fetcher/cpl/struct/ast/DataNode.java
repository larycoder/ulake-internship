package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;

public class DataNode extends ASTNode {
    public Object value;

    public DataNode(Token token, ASTNode... child) {
        super(token, child);
        this.value = token.value;
    }
}
