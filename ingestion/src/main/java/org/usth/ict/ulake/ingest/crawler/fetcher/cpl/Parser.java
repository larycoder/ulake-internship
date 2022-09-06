package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.ASTNode;
import org.usth.ict.ulake.ingest.model.Policy;


public class Parser {
    private Token current_token;
    private Lexer lexer;

    public ASTNode parse(Policy policy) {
        lexer = new Lexer(policy);
        current_token = lexer.getNextToken();
        return execSymbol();
    }

    private void error() {
        throw new CplException("Syntax invalid.");
    }

    private void eat(Type type) {
        if (current_token.type.equals(type)) {
            current_token = lexer.getNextToken();
        } else {
            error();
        }
    }

    private ASTNode execSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.EXEC);

        node.child.add(declareSymbol());
        while (current_token.type == Type.DATA) {
            node.child.add(dataSymbol());
        }
        node.child.add(returnSymbol());

        eat(Type.END);
        return node;
    }

    private ASTNode declareSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.DECLARE);

        varSymbol(node);

        eat(Type.END);
        return node;
    }

    private ASTNode dataSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.DATA);

        if (current_token.type == Type.REQ) {
            node.child.add(reqSymbol());
        } else {
            node.child.add(patternSymbol());
        }
        mapSymbol(node);

        eat(Type.END);
        return node;
    }

    private ASTNode patternSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.PATTERN);

        if (current_token.type == Type.VAR)
            varSymbol(node);

        eat(Type.END);
        return node;
    }

    private ASTNode reqSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.REQ);

        node.child.add(new ASTNode(current_token));
        eat(Type.METHOD);

        node.child.add(pathSymbol());
        while (current_token.type == Type.V_HEAD)
            node.child.add(vheadSymbol());
        if (current_token.type == Type.HEAD)
            node.child.add(headSymbol());
        if (current_token.type == Type.BODY)
            node.child.add(bodySymbol());

        eat(Type.END);
        return node;
    }

    private void mapSymbol(ASTNode node) {
        node.child.add(new ASTNode(current_token));
        eat(Type.MAP);

        while (current_token.type.equals(Type.MAP)) {
            node.child.add(new ASTNode(current_token));
            eat(Type.MAP);
        }
    }

    private ASTNode pathSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.PATH);

        if (current_token.type == Type.VAR)
            varSymbol(node);

        eat(Type.END);
        return node;
    }

    private ASTNode vheadSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.V_HEAD);
        node.child.add(patternSymbol());
        eat(Type.END);
        return node;
    }

    private ASTNode headSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.HEAD);

        if (current_token.type == Type.VAR)
            varSymbol(node);

        eat(Type.END);
        return node;
    }

    private ASTNode bodySymbol() {
        var node = new ASTNode(current_token);
        eat(Type.BODY);

        if (current_token.type == Type.VAR)
            varSymbol(node);

        eat(Type.END);
        return node;
    }

    private void varSymbol(ASTNode node) {
        node.child.add(new ASTNode(current_token));
        eat(Type.VAR);

        while (current_token.type.equals(Type.VAR)) {
            node.child.add(new ASTNode(current_token));
            eat(Type.VAR);
        }
    }

    private ASTNode returnSymbol() {
        var node = new ASTNode(current_token);
        eat(Type.RETURN);

        node.child.add(new ASTNode(current_token));
        eat(Type.METHOD);

        node.child.add(pathSymbol());
        while (current_token.type == Type.V_HEAD)
            node.child.add(vheadSymbol());
        if (current_token.type == Type.HEAD)
            node.child.add(headSymbol());
        if (current_token.type == Type.BODY)
            node.child.add(bodySymbol());

        eat(Type.END);
        return node;
    }
}
