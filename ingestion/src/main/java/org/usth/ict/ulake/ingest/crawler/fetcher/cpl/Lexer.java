package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Token;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.PolicyData;
import org.usth.ict.ulake.ingest.model.PolicyPattern;
import org.usth.ict.ulake.ingest.model.PolicyRequest;

public class Lexer {
    Policy policy;
    List<Token> tokens = new ArrayList<Token>();
    int pos = -1;

    public Lexer(Policy policy) {
        this.policy = policy;
        exec(policy);
    }

    public Token getNextToken() {
        pos += 1;
        if (pos > tokens.size() - 1) {
            return new Token(Type.EOF, "");
        } else {
            return tokens.get(pos);
        }
    }

    private void addToken(Type type, String value) {
        tokens.add(new Token(type, value));
    }

    private void addToken(
        Type type, Map.Entry<String, List<String>> value) {
        tokens.add(new Token(type, value));
    }

    private void exec(Policy policy) {
        addToken(Type.EXEC, "");

        if (policy.declare != null)
            declare(policy.declare);

        if (policy.pipe != null)
            for (String dataKey : policy.pipe.keySet()) {
                addToken(Type.DATA, dataKey);
                data(policy.pipe.get(dataKey));
            }

        if (policy.pReturn != null)
            pReturn(policy.pReturn);

        addToken(Type.END, "");
    }

    private void data(PolicyData policyData) {
        if (policyData.req != null)
            req(policyData.req);

        if (policyData.pattern != null)
            pattern(policyData.pattern);

        if (policyData.map != null) {
            String[] maps = policyData.map.split("\\.");
            for (var myMap : maps)
                addToken(Type.MAP, myMap);
        }

        addToken(Type.END, "");
    }

    private void declare(Map<String, List<String>> body) {
        addToken(Type.DECLARE, "");

        for (var entry : body.entrySet()) {
            addToken(Type.VAR, entry);
        }

        addToken(Type.END, "");
    }

    private void pattern(PolicyPattern policyPattern) {
        addToken(Type.PATTERN, policyPattern.value);

        if (policyPattern.var != null)
            for (var entry : policyPattern.var.entrySet()) {
                addToken(Type.VAR, entry);
            }

        addToken(Type.END, "");
    }

    private void req(PolicyRequest policyReq) {
        addToken(Type.REQ, "");

        if (policyReq.method != null)
            addToken(Type.METHOD, policyReq.method);

        if (policyReq.path != null)
            path(policyReq.path);

        if (policyReq.vhead != null)
            for (var varHead : policyReq.vhead)
                vhead(varHead);

        if (policyReq.head != null)
            head(policyReq.head);

        if (policyReq.body != null)
            body(policyReq.body);

        addToken(Type.END, "");
    }

    private void path(PolicyPattern policyPath) {
        addToken(Type.PATH, policyPath.value);

        if (policyPath.var != null)
            for (var entry : policyPath.var.entrySet()) {
                addToken(Type.VAR, entry);
            }

        addToken(Type.END, "");
    }

    private void body(PolicyPattern policyBody) {
        addToken(Type.BODY, policyBody.value);

        if (policyBody.var != null)
            for (var entry : policyBody.var.entrySet()) {
                addToken(Type.VAR, entry);
            }

        addToken(Type.END, "");
    }

    private void vhead(Map.Entry<String, PolicyPattern> entry) {
        addToken(Type.V_HEAD, entry.getKey());
        pattern(entry.getValue());
        addToken(Type.END, "");
    }

    private void head(Map<String, List<String>> policyHead) {
        addToken(Type.HEAD, "");

        if (policyHead != null)
            for (var entry : policyHead.entrySet()) {
                addToken(Type.VAR, entry);
            }

        addToken(Type.END, "");
    }

    private void pReturn(PolicyRequest policyReturn) {
        addToken(Type.RETURN, "");

        if (policyReturn.method != null)
            addToken(Type.METHOD, policyReturn.method);

        if (policyReturn.path != null)
            path(policyReturn.path);

        if (policyReturn.head != null)
            head(policyReturn.head);

        if (policyReturn.body != null)
            body(policyReturn.body);

        addToken(Type.END, "");
    }
}
