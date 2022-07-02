package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import antlr.collections.AST;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        if(param == null) {
            param = new HashMap<>();
        }
        child = ACT_MAP;
        param.put(name, node);
    }

    public void setChild(ASTNode node) {
        if(list == null) {
            list = new ArrayList<>();
        }
        child = ACT_LIST;
        list.add(node);
    }
}
