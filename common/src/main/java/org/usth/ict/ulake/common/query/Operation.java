package org.usth.ict.ulake.common.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.common.misc.Utils;

public class Operation<T extends Object> implements Queryable {
    public T eq;
    public T neq;
    public T gt;
    public T lt;
    public List<T> in;

    @Override
    public HqlResult getHQL(String property) {
        String clazz = this.getClass().getName().replaceAll("\\.", "_");
        return getHQL(clazz, property);
    }

    protected String where(String property, String opt, String param) {
        return "( " + property + " " + opt + " :" + param + ")";
    }

    protected HqlResult getHQL(String clazz, String property) {
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (eq != null) {
            hql.add(where(property, "=", clazz + "_eq"));
            params.put(clazz + "_eq", eq);
        }

        if (neq != null) {
            hql.add(where(property, "!=", clazz + "_neq"));
            params.put(clazz + "_neq", neq);
        }

        if (gt != null) {
            hql.add(where(property, ">", clazz + "_gt"));
            params.put(clazz + "_gt", gt);
        }

        if (lt != null) {
            hql.add(where(property, "<", clazz + "_lt"));
            params.put(clazz + "_lt", lt);
        }

        if (!Utils.isEmpty(in)) {
            hql.add(where(property, "in", clazz + "_in"));
            params.put(clazz + "_in", in);
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
