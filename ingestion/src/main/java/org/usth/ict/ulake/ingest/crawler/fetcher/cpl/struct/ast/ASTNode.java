package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;

import java.util.List;
import java.util.Map;


public class ASTNode {
    // node type declare
    public static final int AST = 0;
    public static final int ACT = 1;
    public static final int MAP = 2;
    public static final int DATA = 3;

    // child store mode
    public static final int ACT_DEFAULT = 0;
    public static final int ACT_MAP = 1;
    public static final int ACT_LIST = 2;

    public Token token;
    public int node;
    public int child = ACT_DEFAULT;

    public Map<String, ASTNode> param;
    public List<ASTNode> list;

    public ASTNode left;
    public ASTNode right;

    public ASTNode(
            Token token, ASTNode left, ASTNode right) {
        this.token = token;
        this.left = left;
        this.right = right;
    }

    public ASTNode(Token token) {
        this.token = token;
        this.left = this.right = null;
    }

    public void posView() {
        switch (child) {
            case ACT_LIST:
                for(var node : list) {
                    node.posView();
                }
                break;
            case ACT_MAP:
                for(var node: param.values()) {
                    node.posView();
                }
                break;
            default:
                if(left != null) left.posView();
                if(right != null) right.posView();
                break;
        }
        System.out.println(token.toString());
    }
}
