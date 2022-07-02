package org.usth.ict.ulake.ingest.crawler.fetcher.cpl;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.Type;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.ASTNode;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.ast.DataNode;
import org.usth.ict.ulake.ingest.utils.RestClientUtil;
import org.usth.ict.ulake.ingest.utils.TransferUtil;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    // stack declare
    private final String RESULT_STACK = "RESULT_STACK";
    private final String TEMP_STACK = "TEMP_STACK";
    private final String VAR_STACK = "VAR_STACK";

    ASTNode tree;
    Map<String, Object> stack = new HashMap<>();
    RestClientUtil client;

    public Interpreter(RestClientUtil client) {
        this.client = client;

        stack.put(RESULT_STACK, new TableStruct());
        stack.put(TEMP_STACK, new ArrayList<>());
        stack.put(VAR_STACK, new HashMap<>());
    }

    public Interpreter() {
        this(new RestClientUtil());
    }

    public TableStruct eval(Map policy) {
        Parser parser = new Parser();
        tree = parser.parse(policy);

        getTempStack().clear();
        getVarStack().clear();
        getResultStack().drop();

        visit(tree);
        return getResultStack().clone();
    }

    //============================================//
    // Interpreter supporter definition

    private void error() {
        throw new CplException("Runtime error.");
    }

    private void error(String message) {
        throw new CplException("Runtime error: " + message);
    }

    private TableStruct getResultStack() {
        return (TableStruct) stack.get(RESULT_STACK);
    }

    private List getTempStack() {
        return (List) stack.get(TEMP_STACK);
    }

    private Map getVarStack() {
        return (Map) stack.get(VAR_STACK);
    }

    private Object visit(ASTNode tree) {
        if(tree.node == ASTNode.ACT) return visitAct(tree);
        else if(tree.node == ASTNode.DATA) return visitData(tree);
        else if(tree.node == ASTNode.MAP) return visitMap(tree);
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
        if(node.child != ASTNode.ACT_DEFAULT) error("MAP structure is wrong.");
        if(node.left != null) visit(node.left);

        List dataList = getTempStack();
        List interList = new ArrayList();

        // prepare list
        for(var data : dataList) {
            // unwind data
            if(data instanceof List) {
                interList.addAll((List) data);
            } else {
                interList.add(data);
            }
        }
        dataList.clear();

        // map list
        for(var data : interList) {
            if(data instanceof Map) {
                Map map = (Map) data;
                dataList.add(map.get(node.token.value));
            }
        }

        return null;
    }

    private Object visitActVAR(ASTNode node) {
        if(node.left != null) {
            // put data to stack for process
            if(node.left.node == ASTNode.DATA) {
                List temp = getTempStack();
                temp.add(visit(node.left));
            } else {
                visit(node.left);
            }
        } else {
            error("Act VAR struct is wrong.");
        }

        Map<String, String> var = getVarStack();
        var mapInfo = (Map<String, String>) node.token.value;
        List temp = getTempStack();
        List inter = new ArrayList();

        inter.addAll(temp);
        temp.clear();

        // prepare variable
        Map<String, String> interVar = new HashMap<>();
        for(String key : mapInfo.keySet()) {
            String newKey = "\\{" + key + "\\}";
            interVar.put(newKey, var.get(mapInfo.get(key)));
        }

        // process string only
        while(inter.size() > 0) {
            var pattern = (String) inter.remove(0);
            for(String key : interVar.keySet()) {
                pattern = pattern.replaceAll(key, interVar.get(key));
            }
            temp.add(pattern);
        }

        return null;
    }

    private Object visitActPATH(ASTNode node) {
        if(node.left == null) {
            error("Act PATH Struct is wrong.");
        } else if(node.left.node == ASTNode.DATA) {
            getTempStack().add(visit(node.left));
        } else {
            visit(node.left);
        }
        return null;
    }

    private Object visitActREQ(ASTNode node) {
        if(node.child != ASTNode.ACT_LIST) error("Act REQ structure is wrong.");
        RestClientUtil client = this.client.clone();

        List temp = getTempStack();
        String method = "GET";
        Map body = null; // support json only
        List<String> path = new ArrayList<>();

        // prepare variable setup
        for(var value : node.list) {
            if(value.token.type.equals(Type.METHOD)) {
                method = (String) visit(value);
            } else if(value.token.type.equals(Type.HEAD)) {
                var heads = (Map<String, Object>) visit(value);
                client.setHead(heads);
            } else if(value.token.type.equals(Type.BODY)) {
                body = (Map) visit(value);
            } else if(value.token.type.equals(Type.PATH)) {
                visit(value);
                path.addAll(getTempStack());
                temp.clear();
            }
        }

        // invoke request call
        for(var concrete : path) {
            Response resp;
            var request = client.clone()
                    .setPath(concrete)
                    .buildRequest();
            if(body == null) {
                resp = request.method(method);
            } else {
                // support send json body only
                Entity entity = Entity.entity(body, MediaType.APPLICATION_JSON);
                resp = request.method(method, entity);
            }

            // push result back to temp stack
            var is = (InputStream) resp.getEntity();
            Map data = null;
            try {
                data = new TransferUtil().streamToJson(is);
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(data != null) temp.add(data);
        }
        return null;
    }

    private Object visitActPATTERN(ASTNode node) {
        if(node.child != ASTNode.ACT_DEFAULT) error("Act PATTERN structure is wrong");

        if(node.left.node == ASTNode.DATA) {
            getTempStack().add(visit(node.left));
        } else {
            visit(node.left);
        }

        List temp = getTempStack();
        List inter = new ArrayList();

        inter.addAll(temp);
        temp.clear();

        for(var obj : inter) {
            Map data = new HashMap();
            data.put("data", obj);
            temp.add(data);
        }

        return null;
    }

    private Object visitActDATA(ASTNode node) {
        if(node.child != ASTNode.ACT_DEFAULT) error("Act DATA structure is wrong.");

        String name = (String) node.token.value;
        Map varMap;
        ASTNode actTree = node.left;
        ASTNode mapTree = node.right;

        Map varStack = getVarStack();
        List tempStack = getTempStack();
        TableStruct resultStack = getResultStack();
        TableStruct interStack = resultStack.clone();

        tempStack.clear();
        resultStack.clear();
        resultStack.addKey(name);

        while(interStack.rowSize() > 0) {
            tempStack.clear();
            varStack.clear();

            varMap = interStack.stackPopJson();
            varStack.putAll(varMap);

            // call request and map result to temp stack
            visit(actTree);
            visit(mapTree);

            for(var value : tempStack) {
                varMap.put(name, value);
                resultStack.add(varMap);
            }
        }

        return null;
    }

    private Object visitActKEY(ASTNode node) {
        if(node.left.node != ASTNode.DATA) error("Act KEY structure is wrong.");
        List values = (List) visit(node.left);
        String key = (String) node.token.value;
        List var = new ArrayList();

        for(var data : values) {
            Map newVar = new HashMap();
            newVar.put(key, data);
            var.add(newVar);
        }

        return var;
    }

    private Object visitActDECLARE(ASTNode node) {
        if(node.child != ASTNode.ACT_LIST) error("Act DECLARE structure is wrong.");
        List<Map> listVar = new ArrayList<>();
        List<Map> listFinalVar = new ArrayList<>();
        List<Map> listNewVar;

        TableStruct result = getResultStack();
        result.drop();

        for(ASTNode var : node.list) {
            // declare var and add first var to list
            if(!var.token.type.equals(Type.KEY)) continue;
            if(listVar.isEmpty()) {
                listVar.addAll((List) visit(var));
                continue;
            }

            listNewVar = (List) visit(var);
            if(listNewVar == null) continue;

            for(Map mapVar : listVar) {
                for(Map newMapVar : listNewVar) {
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

        for(Map data : listVar) {
            if(result.colSize() == 0) {
                List fields = new ArrayList(data.keySet());
                result.setKey(fields);
            }
            result.add(data);
        }

        return null;
    }

    private Object visitActEXEC(ASTNode node) {
        if(node.child != ASTNode.ACT_LIST) error("Act EXEC structure is wrong.");

        for(ASTNode child : node.list) {
            // Deactivate return token for now
            if(child.token.type.equals(Type.RETURN)) continue;

            getTempStack().clear();
            getVarStack().clear();
            visit(child);
        }
        return null;
    }

    private Object visitAct(ASTNode node) {
        if(node.token.type.equals(Type.PATH)) return visitActPATH(node);
        else if(node.token.type.equals(Type.VAR)) return visitActVAR(node);
        else if(node.token.type.equals(Type.REQ)) return visitActREQ(node);
        else if(node.token.type.equals(Type.PATTERN)) return visitActPATTERN(node);
        else if(node.token.type.equals(Type.DATA)) return visitActDATA(node);
        else if(node.token.type.equals(Type.KEY)) return visitActKEY(node);
        else if(node.token.type.equals(Type.DECLARE)) return visitActDECLARE(node);
        else if(node.token.type.equals(Type.EXEC)) return visitActEXEC(node);
        else error("Act node type not found !");
        return null;
    }
}
