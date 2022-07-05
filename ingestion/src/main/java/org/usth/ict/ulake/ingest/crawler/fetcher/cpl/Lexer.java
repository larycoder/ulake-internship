package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    Map<String, Object> policy;
    List<Token> tokens = new ArrayList<Token>();
    int pos = -1;

    public Lexer(Map<String, Object> policy){
        this.policy = policy;
        buildTokenList();
    }

    public Token getNextToken() {
        pos += 1;
        if(pos > tokens.size() - 1) {
            return new Token(Type.EOF, null);
        } else {
            return tokens.get(pos);
        }
    }

    private void error() {
        throw new CplException("Unrecognized token.");
    }

    private void addToken(Type type, Object value) {
        tokens.add(new Token(type, value));
    }

    private void declareTokens(Map<String, List<String>> body) {
        addToken(Type.DECLARE, null);

        for(String key: body.keySet()) {
            addToken(Type.KEY, key);
            addToken(Type.VALUE, body.get(key));
        }

        addToken(Type.END, null);
    }

    /*
     * This method used to extract
     * common value - var pattern
     *
     */
    private void varTokens(Map<String, Object> body) {
        for(String key : body.keySet()) {
            if(key.equals("value")) {
                addToken(Type.VALUE, body.get(key));
            } else if(key.equals("var")) {
                var vars = (Map) body.get(key);
                for(var varKey : vars.keySet()) {
                    Map<Object, Object> varMap = new HashMap<>();
                    varMap.put(varKey, vars.get(varKey));
                    addToken(Type.VAR, varMap);
                }
            }
        }
    }

    private void pathTokens(Map<String, Object> body) {
        addToken(Type.PATH, null);
        varTokens(body);
        addToken(Type.END, null);
    }

    private void reqTokens(Map<String, Object> body) {
        addToken(Type.REQ, null);

        for(String key : body.keySet()) {
            if(key.equals("method")) {
                addToken(Type.METHOD, body.get(key));
            } else if(key.equals("head")) {
                addToken(Type.HEAD, body.get(key));
            } else if(key.equals("body")) {
                addToken(Type.BODY, body.get(key));
            } else if(key.equals("path")) {
                pathTokens((Map) body.get(key));
            } else {
                error();
            }
        }

        addToken(Type.END, null);
    }

    private void patternTokens(Map<String, Object> body) {
        addToken(Type.PATTERN, null);
        varTokens(body);
        addToken(Type.END, null);
    }

    private void mapTokens(String map) {
        String[] mapList = map.split("\\.");
        for(var mapValue : mapList) {
            addToken(Type.MAP, mapValue);
        }
    }

    private void dataTokens(String name, Map<String, Object> body) {
        addToken(Type.DATA, name);

        for(String key : body.keySet()) {
            if(key.equals("req")) {
                reqTokens((Map) body.get(key));
            } else if(key.equals("map")) {
                mapTokens((String) body.get(key));
            } else if(key.equals("pattern")) {
                patternTokens((Map) body.get(key));
            } else {
                error();
            }
        }

        addToken(Type.END, null);
    }

    private void buildTokenList() {
        addToken(Type.EXEC, null);

        for(String key : policy.keySet()) {
            if(key.equals("declare")) {
                declareTokens((Map) policy.get(key));
            } else if(key.equals("pipe")) {
                var data = (Map<String, Object>) policy.get(key);
                for(String dataKey : data.keySet()) {
                    dataTokens(dataKey, (Map) data.get(dataKey));
                }
            } else if(key.equals("pReturn")){
                addToken(Type.RETURN, null);
            } else {
                error();
            }
        }

        addToken(Type.END, null);
    }
}
