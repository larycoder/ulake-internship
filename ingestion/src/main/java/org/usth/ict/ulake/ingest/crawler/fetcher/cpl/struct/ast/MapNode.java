package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;

public class MapNode extends ASTNode {
    public MapNode(
            Token token, ASTNode left, ASTNode right) {
        super(token, left, right);
        this.node = MAP;
    }

    public MapNode(Token token) {
        this(token, null, null);
    }
}
