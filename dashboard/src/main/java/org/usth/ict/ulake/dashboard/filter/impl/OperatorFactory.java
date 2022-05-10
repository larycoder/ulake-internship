package org.usth.ict.ulake.dashboard.filter.impl;

import java.util.HashMap;
import java.util.Map;

import org.usth.ict.ulake.dashboard.filter.Operator;
import org.usth.ict.ulake.dashboard.filter.impl.op.Equal;
import org.usth.ict.ulake.dashboard.filter.impl.op.GreaterThan;
import org.usth.ict.ulake.dashboard.filter.impl.op.LessThan;
import org.usth.ict.ulake.dashboard.filter.impl.op.Like;

public class OperatorFactory {
    private static Map <String, Operator<?, ?, ?>> mapper = new HashMap<>();

    public static Operator<?, ?, ?> getOp(String op) {
        if (mapper.isEmpty()) {
            {
                mapper.put("=", new Equal());
                mapper.put(">", new GreaterThan());
                mapper.put("<", new LessThan());
                mapper.put("like", new Like());
            }
        }
        return mapper.get(op);
    }
}
