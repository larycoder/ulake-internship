package org.usth.ict.ingest.crawler.fetcher.cpl;

import org.usth.ict.ingest.crawler.fetcher.cpl.struct.Token;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.ast.ASTNode;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.ast.ActNode;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.ast.DataNode;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.ast.MapNode;

import java.util.Map;


public class Parser {
    private Token current_token;
    private Lexer lexer;

    public ASTNode parse(Map policy) {
        lexer = new Lexer(policy);
        current_token = lexer.getNextToken();
        return execSymbol();
    }

    private void error() {
        throw new CplException("Syntax invalid.");
    }

    private void eat(String type) {
        if(current_token.type.equals(type)) {
            current_token = lexer.getNextToken();
        } else {
            error();
        }
    }

    private ASTNode returnSymbol() {
        Token token = current_token;
        eat(Type.RETURN);
        return new ActNode(token);
    }

    private ASTNode mapSymbol() {
        MapNode node = new MapNode(current_token);
        eat(Type.MAP);
        while(current_token.type.equals(Type.MAP)) {
            node = new MapNode(current_token, node, null);
            eat(Type.MAP);
        }
        return node;
    }

    private ASTNode varSymbol() {
        ASTNode node = new DataNode(current_token);
        eat(Type.VALUE);

        while(current_token.type.equals(Type.VAR)) {
            node = new ActNode(current_token, node, null);
            eat(Type.VAR);
        }

        return node;
    }

    private ASTNode pathSymbol() {
        Token pathToken = current_token;
        eat(Type.PATH);
        ASTNode node = varSymbol();
        eat(Type.END);
        return new ActNode(pathToken, node, null);
    }

    private ASTNode reqSymbol() {
        ActNode node = new ActNode(current_token);
        eat(Type.REQ);

        node.setChild(new DataNode(current_token));
        eat(Type.METHOD);

        node.setChild(pathSymbol());

        if(current_token.type.equals(Type.HEAD)) {
            node.setChild(new DataNode(current_token));
            eat(Type.HEAD);
        }
        if(current_token.type.equals(Type.BODY)) {
            node.setChild(new DataNode(current_token));
            eat(Type.BODY);
        }
        eat(Type.END);
        return node;
    }

    private ASTNode patternSymbol() {
        Token pattern = current_token;

        eat(Type.PATTERN);
        ASTNode node = varSymbol();
        eat(Type.END);

        return new ActNode(pattern, node, null);
    }

    private ASTNode dataSymbol() {
        Token dataToken = current_token;
        eat(Type.DATA);

        ASTNode nodeAct;
        if(current_token.type.equals(Type.REQ)) {
            nodeAct = reqSymbol();
        } else {
            nodeAct = patternSymbol();
        }
        ASTNode nodeMap = mapSymbol();
        eat(Type.END);
        return new ActNode(dataToken, nodeAct, nodeMap);
    }

    private ASTNode pareSymbol() {
        Token key = current_token;
        eat(Type.KEY);

        DataNode node = new DataNode(current_token);
        eat(Type.VALUE);
        return new ActNode(key, node, null);
    }

    private ASTNode declareSymbol() {
        ActNode node = new ActNode(current_token);
        eat(Type.DECLARE);

        node.setChild(pareSymbol());
        while(current_token.type.equals(Type.KEY)) {
            node.setChild(pareSymbol());
        }
        eat(Type.END);
        return node;
    }

    private ASTNode execSymbol() {
        ActNode node = new ActNode(current_token);
        eat(Type.EXEC);

        node.setChild(declareSymbol());
        while(current_token.type.equals(Type.DATA)) {
            node.setChild(dataSymbol());
        }
        if(current_token.type.equals(Type.RETURN)) {
            node.setChild(returnSymbol());
        }
        eat(Type.END);
        return node;
    }
}
