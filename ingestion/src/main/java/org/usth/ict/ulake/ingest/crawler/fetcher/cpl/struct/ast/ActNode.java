package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import java.util.ArrayList;
import java.util.HashMap;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;


public class ActNode extends ASTNode {
    public ActNode(
        Token token, ASTNode left, ASTNode right) {
        super(token, left, right);
        this.node = ACT;
    }

    public ActNode(Token token) {
        this(token, null, null);
    }

    public void setChild(String name, ASTNode node) {
        if (param == null) {
            param = new HashMap<>();
        }
        child = ACT_MAP;
        param.put(name, node);
    }

    public void setChild(ASTNode node) {
        if (list == null) {
            list = new ArrayList<>();
        }
        child = ACT_LIST;
        list.add(node);
    }
}
