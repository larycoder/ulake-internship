package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.ASTNode;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.DataNode;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;
import org.usth.ict.ulake.ingest.utils.LakeHttpClient;

public class Interpreter {
    /**
     * Memory stack for processing language.
     * */
    private class BufferStack {
        public TableStruct<String> RESULT_STACK;
        public List<Object> TEMP_STACK;
        public Map<String, Object> VAR_STACK;
    }

    ASTNode tree;
    BufferStack stack;
    HttpRawRequest client;

    public Interpreter(HttpRawRequest client) {
        this.client = client;
        stack = new BufferStack();
        stack.RESULT_STACK = new TableStruct<String>();
        stack.TEMP_STACK = new ArrayList<Object>();
        stack.VAR_STACK = new HashMap<String, Object>();
    }

    public Interpreter() {
        this(new HttpRawRequest());
    }

    public TableStruct<String> eval(Map<String, Object> policy) {
        Parser parser = new Parser();
        tree = parser.parse(policy);

        stack.TEMP_STACK.clear();
        stack.VAR_STACK.clear();
        stack.RESULT_STACK.drop();

        visit(tree);
        return stack.RESULT_STACK.clone();
    }

    //============================================//
    // Interpreter supporter definition

    private void error(String message) {
        throw new CplException("Runtime error: " + message);
    }

    private Object visit(ASTNode tree) {
        if (tree.node == ASTNode.ACT) return visitAct(tree);
        else if (tree.node == ASTNode.DATA) return visitData(tree);
        else if (tree.node == ASTNode.MAP) return visitMap(tree);
        else error("Node type not found !");
        return null;
    }

    //============================================//
    // Node action definition
    private Object visitData(ASTNode node) {
        DataNode data = (DataNode) node;
        return data.value;
    }

    private Object visitMap(ASTNode node) {
        if (node.child != ASTNode.ACT_DEFAULT) error("MAP structure is wrong.");
        if (node.left != null) visit(node.left);

        List<Object> dataList = stack.TEMP_STACK;
        List<Object> interList = new ArrayList<>();

        // prepare list
        for (var data : dataList) {
            // unwind data
            if (data instanceof List) {
                interList.addAll((List<Object>)data);
            } else {
                interList.add(data);
            }
        }
        dataList.clear();

        // map list
        for (var data : interList) {
            if (data instanceof Map) {
                var map = (Map) data;
                dataList.add(map.get(node.token.value));
            }
        }

        return null;
    }

    private Object visitActVAR(ASTNode node) {
        if (node.left != null) {
            // put data to stack for process
            if (node.left.node == ASTNode.DATA) {
                List<Object> temp = stack.TEMP_STACK;
                temp.add(visit(node.left));
            } else {
                visit(node.left);
            }
        } else {
            error("Act VAR struct is wrong.");
        }

        Map<String, String> var = (Map<String, String>) (Object) stack.VAR_STACK;
        var mapInfo = (Map<String, String>) node.token.value;
        List<Object> temp = stack.TEMP_STACK;
        List<Object> inter = new ArrayList<>();

        inter.addAll(temp);
        temp.clear();

        // prepare variable
        Map<String, String> interVar = new HashMap<>();
        for (String key : mapInfo.keySet()) {
            String newKey = "\\{" + key + "\\}";
            interVar.put(newKey, var.get(mapInfo.get(key)));
        }

        // process string only
        while (inter.size() > 0) {
            var pattern = (String) inter.remove(0);
            for (String key : interVar.keySet()) {
                pattern = pattern.replaceAll(key, interVar.get(key));
            }
            temp.add(pattern);
        }

        return null;
    }

    private Object visitActPATH(ASTNode node) {
        if (node.left == null) {
            error("Act PATH Struct is wrong.");
        } else if (node.left.node == ASTNode.DATA) {
            stack.TEMP_STACK.add(visit(node.left));
        } else {
            visit(node.left);
        }
        return null;
    }

    private Object visitActREQ(ASTNode node) {
        if (node.child != ASTNode.ACT_LIST) error("Act REQ structure is wrong.");
        // TODO: improve client request mechanism
        HttpRawRequest client = this.client.clone();

        client.method = HttpMethod.GET;
        Map<Object, Object> body = null;
        List<String> path = new ArrayList<>();

        List<Object> temp = stack.TEMP_STACK;

        // prepare variable setup
        for (var value : node.list) {
            if (value.token.type.equals(Type.METHOD)) {
                client.method = (String) visit(value);
            } else if (value.token.type.equals(Type.HEAD)) {
                var heads = (Map<String, String>) visit(value);
                for (String key : heads.keySet())
                    client.addHeader(key, heads.get(key));
            } else if (value.token.type.equals(Type.BODY)) {
                body = (Map) visit(value);
            } else if (value.token.type.equals(Type.PATH)) {
                visit(value);
                path.addAll((List<String>)(Object) stack.TEMP_STACK);
                temp.clear();
            }
        }

        // invoke request call
        for (String concrete : path) {
            var request = client.clone();
            request.path = concrete;

            if (body != null) {
                try {
                    request.body = new ObjectMapper().writeValueAsString(body);
                    request.addHeader("Content-Type", "application/json");
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            HttpRawResponse resp = LakeHttpClient.send(request);

            // push result back to temp stack
            try {
                var data = new ObjectMapper().readValue(resp.body, Map.class);
                temp.add(data);
                resp.body.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object visitActPATTERN(ASTNode node) {
        if (node.child != ASTNode.ACT_DEFAULT) error("Act PATTERN structure is wrong");

        if (node.left.node == ASTNode.DATA) {
            stack.TEMP_STACK.add(visit(node.left));
        } else {
            visit(node.left);
        }

        List temp = stack.TEMP_STACK;
        List inter = new ArrayList();

        inter.addAll(temp);
        temp.clear();

        for (var obj : inter) {
            Map data = new HashMap();
            data.put("data", obj);
            temp.add(data);
        }

        return null;
    }

    private Object visitActDATA(ASTNode node) {
        if (node.child != ASTNode.ACT_DEFAULT) error("Act DATA structure is wrong.");

        String name = (String) node.token.value;
        Map varMap;
        ASTNode actTree = node.left;
        ASTNode mapTree = node.right;

        Map varStack = stack.VAR_STACK;
        List tempStack = stack.TEMP_STACK;
        TableStruct resultStack = stack.RESULT_STACK;
        TableStruct interStack = resultStack.clone();

        tempStack.clear();
        resultStack.clear();
        resultStack.addKey(name);

        while (interStack.rowSize() > 0) {
            tempStack.clear();
            varStack.clear();

            varMap = interStack.stackPopJson();
            varStack.putAll(varMap);

            // call request and map result to temp stack
            visit(actTree);
            visit(mapTree);

            for (var value : tempStack) {
                varMap.put(name, value);
                resultStack.add(varMap);
            }
        }

        return null;
    }

    private Object visitActKEY(ASTNode node) {
        if (node.left.node != ASTNode.DATA) error("Act KEY structure is wrong.");
        List values = (List) visit(node.left);
        String key = (String) node.token.value;
        List var = new ArrayList();

        for (var data : values) {
            Map newVar = new HashMap();
            newVar.put(key, data);
            var.add(newVar);
        }

        return var;
    }

    private Object visitActDECLARE(ASTNode node) {
        if (node.child != ASTNode.ACT_LIST) error("Act DECLARE structure is wrong.");
        List<Map> listVar = new ArrayList<>();
        List<Map> listFinalVar = new ArrayList<>();
        List<Map> listNewVar;

        TableStruct result = stack.RESULT_STACK;
        result.drop();

        for (ASTNode var : node.list) {
            // declare var and add first var to list
            if (!var.token.type.equals(Type.KEY)) continue;
            if (listVar.isEmpty()) {
                listVar.addAll((List) visit(var));
                continue;
            }

            listNewVar = (List) visit(var);
            if (listNewVar == null) continue;

            for (Map mapVar : listVar) {
                for (Map newMapVar : listNewVar) {
                    Map newData = new HashMap();
                    newData.putAll(mapVar);
                    newData.putAll(newMapVar);
                    listFinalVar.add(newData);
                }
            }
            listVar.clear();
            listVar.addAll(listFinalVar);
            listFinalVar.clear();
        }

        for (Map data : listVar) {
            if (result.colSize() == 0) {
                List fields = new ArrayList(data.keySet());
                result.setKey(fields);
            }
            result.add(data);
        }

        return null;
    }

    private Object visitActEXEC(ASTNode node) {
        if (node.child != ASTNode.ACT_LIST) error("Act EXEC structure is wrong.");

        for (ASTNode child : node.list) {
            // Deactivate return token for now
            if (child.token.type.equals(Type.RETURN)) continue;

            stack.TEMP_STACK.clear();
            stack.VAR_STACK.clear();
            visit(child);
        }
        return null;
    }

    private Object visitAct(ASTNode node) {
        if (node.token.type.equals(Type.PATH)) return visitActPATH(node);
        else if (node.token.type.equals(Type.VAR)) return visitActVAR(node);
        else if (node.token.type.equals(Type.REQ)) return visitActREQ(node);
        else if (node.token.type.equals(Type.PATTERN)) return visitActPATTERN(node);
        else if (node.token.type.equals(Type.DATA)) return visitActDATA(node);
        else if (node.token.type.equals(Type.KEY)) return visitActKEY(node);
        else if (node.token.type.equals(Type.DECLARE)) return visitActDECLARE(node);
        else if (node.token.type.equals(Type.EXEC)) return visitActEXEC(node);
        else error("Act node type not found !");
        return null;
    }
}
