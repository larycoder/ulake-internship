package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;


public class ActNode extends ASTNode {
    public ActNode(Token token, ASTNode... child) {
        super(token, child);
    }

    public void addChild(ASTNode node) {
        child.add(node);
    }
}
