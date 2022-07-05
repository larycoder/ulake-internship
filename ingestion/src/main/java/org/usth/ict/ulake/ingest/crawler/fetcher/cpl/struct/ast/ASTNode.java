package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;


public class ASTNode {
    public Token token;
    public List<ASTNode> child;

    public ASTNode(Token token, ASTNode... child) {
        this.token = token;
        if (child != null && child.length > 0)
            this.child = Arrays.asList(child);
        else
            this.child = new ArrayList<>();
    }

    public ASTNode getChild(int idx) {
        if (child.size() <= idx)
            return null;
        else
            return child.get(idx);
    }

    public void posView() {
        for (var node : child)
            node.posView();
        System.out.println(token.toString());
    }
}
