package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.ASTNode;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;
import org.usth.ict.ulake.ingest.utils.LakeHttpClient;

public class Interpreter {
    /**
     * Memory stack for processing language.
     * */
    private class BufferStack {
        // store all result of pipeline process
        public TableStruct<String> RESULT_STACK;

        // store temporary list of data during processing
        public List<Object> TEMP_STACK;

        // store key value pair for each row of result table during processing
        public Map<String, String> VAR_STACK;
    }

    private BufferStack stack;
    private HttpRawRequest client;
    private ObjectMapper mapper;

    public ASTNode ret; // return node after parsing

    public Interpreter(HttpRawRequest client) {
        this.client = client;
        stack = new BufferStack();
        stack.RESULT_STACK = new TableStruct<String>();
        stack.TEMP_STACK = new ArrayList<Object>();
        stack.VAR_STACK = new HashMap<String, String>();
        mapper = new ObjectMapper();
    }

    public Interpreter() {
        this(new HttpRawRequest());
    }

    /**
     * Execute and generate table from policy.
     * Special Behavior: do not process
     * */
    public TableStruct<String> eval(Policy policy) {
        Parser parser = new Parser();
        ASTNode tree = parser.parse(policy);

        stack.TEMP_STACK.clear();
        stack.VAR_STACK.clear();
        stack.RESULT_STACK.drop();

        visit(tree);
        return stack.RESULT_STACK.clone();
    }

    private void error(String message) {
        throw new CplException("Runtime error: " + message);
    }

    private void visit(ASTNode tree) {
        if (tree.type == Type.EXEC) visitExec(tree);
        else if (tree.type == Type.DECLARE) visitDeclare(tree);
        else if (tree.type == Type.DATA) visitData(tree);
        else if (tree.type == Type.PATTERN) visitPattern(tree);
        else if (tree.type == Type.REQ) visitReq(tree);
        else if (tree.type == Type.MAP) visitMap(tree);
        else if (tree.type == Type.VAR) visitVar(tree);
        else if (tree.type == Type.RETURN) ret = tree; // process independent
        else error("Node type not found, receive: " + tree.token.toString());
    }

    //============================================//
    // Interpreter supporter definition

    private void visitExec(ASTNode node) {
        for (ASTNode child : node.child)
            visit(child);
    }

    private void visitDeclare(ASTNode node) {
        for (ASTNode child : node.child) {
            var value = child.token.mapValue;
            stack.RESULT_STACK.add(
                value.getKey(), value.getValue());
        }
    }

    private void visitData(ASTNode node) {
        // new field to fields
        List<String> key = stack.RESULT_STACK.getKey();
        key.add(node.token.stringValue);
        var newTableResult = new TableStruct<String>(key);

        // update new field values with each table row
        while (stack.RESULT_STACK.rowSize() > 0) {
            // store current table row for processing
            stack.VAR_STACK = stack.RESULT_STACK.queuePopJson();

            for (ASTNode child : node.child)
                visit(child);

            // add processed values with corresponding row old values to table
            while (!stack.TEMP_STACK.isEmpty()) {
                stack.VAR_STACK.put(node.token.stringValue,
                                    stack.TEMP_STACK.remove(0).toString());
                newTableResult.add(stack.VAR_STACK);
            }
        }

        stack.RESULT_STACK = newTableResult;
    }

    private void visitPattern(ASTNode node) {
        stack.TEMP_STACK.add(node.token.stringValue);

        for (ASTNode child : node.child)
            visit(child);

        // wrap value to data holder
        for (int i = 0; i < stack.TEMP_STACK.size(); i++) {
            var valueHolder = new HashMap<String, Object>();
            valueHolder.put("data", stack.TEMP_STACK.get(i));
            stack.TEMP_STACK.set(i, valueHolder);
        }
    }

    private void visitReq(ASTNode node) {
        List<HttpRawRequest> req = parseNodeToReq(node);
        while (!req.isEmpty()) {
            // parse and put result values to temp stack
            HttpRawResponse resp = LakeHttpClient.send(req.remove(0));
            try {
                String respBody = new String(
                    resp.body.readAllBytes(), StandardCharsets.UTF_8);
                resp.body.close();

                // try to save response as Map value
                // if could not convert to Map then save raw String
                var holder = new HashMap<String, Object>();
                try {
                    var respMap = mapper.readValue(respBody, Map.class);
                    holder.put("data", respMap);
                } catch (JsonProcessingException e) {
                    holder.put("data", respBody);
                }

                stack.TEMP_STACK.add(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method to parse request node to http request list.
     * */
    private List<HttpRawRequest> parseNodeToReq(ASTNode node) {
        List<HttpRawRequest> req = new ArrayList<>();
        List<String> path = new ArrayList<>();
        List<String> body = new ArrayList<>();
        HttpRawRequest client = this.client.clone();

        // collect request parameter
        for (ASTNode child : node.child) {
            if (child.type == Type.METHOD) {
                client.method = child.token.stringValue;
            } else if (child.type == Type.PATH) {
                parseReqStringPattern(child);
                while (!stack.TEMP_STACK.isEmpty())
                    path.add(stack.TEMP_STACK.remove(0).toString());
            } else if (child.type == Type.BODY) {
                parseReqStringPattern(child);
                while (!stack.TEMP_STACK.isEmpty())
                    body.add(stack.TEMP_STACK.remove(0).toString());
            } else if (child.type == Type.HEAD) {
                client.headers.putAll(visitHead(child));
            }
        }

        // build list of parameter
        TableStruct<String> reqParam = new TableStruct<>();
        reqParam.add("path", path);
        reqParam.add("body", body);

        while (reqParam.rowSize() > 0) {
            Map<String, String> param = reqParam.queuePopJson();
            var request = client.clone();
            request.addPath(param.get("path"));
            request.body = param.get("body");
            req.add(request);
        }
        return req;
    }

    /**
     * Helper method to parse request pattern to request values.
     * */
    private void parseReqStringPattern(ASTNode node) {
        stack.TEMP_STACK.add(node.token.stringValue);
        for (ASTNode child : node.child)
            visit(child);
        for (int i = 0; i < stack.TEMP_STACK.size(); i++) {
            stack.TEMP_STACK.set(i, stack.TEMP_STACK.get(i).toString());
        }
    }

    private Map<String, List<String>> visitHead(ASTNode node) {
        Map<String, List<String>> holder = new HashMap<>();
        for (ASTNode child : node.child) {
            var value = child.token.mapValue;
            holder.put(value.getKey(), value.getValue());
        }
        return holder;
    }

    private void visitMap(ASTNode node) {
        List<Object> myList = new ArrayList<>();

        // unwind list data
        while (!stack.TEMP_STACK.isEmpty()) {
            var data = stack.TEMP_STACK.remove(0);
            if (data instanceof List) {
                var type = new TypeReference<List<Object>>() {};
                var myData = mapper.convertValue(data, type);
                myList.addAll(myData);
            } else {
                myList.add(data);
            }
        }

        // collect new data by map key
        // TODO: extend more type than Map type
        while (!myList.isEmpty()) {
            var data = myList.remove(0);
            if (data instanceof Map) {
                var map = mapper.convertValue(data, Map.class);
                stack.TEMP_STACK.add(map.get(node.token.stringValue));
            }
        }
    }

    /**
     * Replace all pattern string by new value.
     * Error behavior: if fail on value processing, the original is hold
     * */
    private void visitVar(ASTNode node) {
        var myList = new ArrayList<Object>();
        var valueList = new ArrayList<String>();
        var holder = new HashMap<String, Object>();
        var type = new TypeReference<Map<String, Object>>() {};

        String key = "\\{" + node.token.mapValue.getKey() + "\\}";

        // detect and change variable to mere string
        for (String value : node.token.mapValue.getValue()) {
            if (value.startsWith("$"))
                valueList.add(stack.VAR_STACK.get(value));
            else
                valueList.add(value);
        }

        // replace value by pattern
        while (!stack.TEMP_STACK.isEmpty()) {
            holder.put("temp", stack.TEMP_STACK.remove(0));
            try {
                // put data to holder for compatible with JSON
                String json = mapper.writeValueAsString(holder);

                // replace by pattern and retrieve data from holder
                // WARNING: process erases type of original object
                for (String rplc : valueList) {
                    String newJson = json.replaceAll(key, rplc);
                    holder.putAll(mapper.readValue(newJson, type));
                    myList.add(holder.get("temp"));
                }
            } catch (JsonProcessingException e) {
                myList.add(holder.get("temp"));
                holder.clear();
            }
        }
        stack.TEMP_STACK.addAll(myList);
    }

    /**
     * Convert return node to list http request.
     * @param node return node
     * @param param parameter to process var in node
     * */
    public List<HttpRawRequest> visitReturn(
        ASTNode node, Map<String, String> param) {
        stack.VAR_STACK = param;
        return parseNodeToReq(node);
    }
}
