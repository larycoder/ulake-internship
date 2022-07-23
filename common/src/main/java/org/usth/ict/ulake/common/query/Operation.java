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
        String clazz = this.getClass().getName();
        return getHQL(clazz, property);
    }

    protected String where(String property, String opt, String param) {
        return "( " + property + " " + opt + " :" + param + ")";
    }

    protected HqlResult getHQL(String clazz, String property) {
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (eq != null) {
            hql.add(where(property, "=", clazz + ".eq"));
            params.put(clazz + ".eq", eq);
        }

        if (neq != null) {
            hql.add(where(property, "!=", clazz + ".neq"));
            params.put(clazz + ".neq", neq);
        }

        if (gt != null) {
            hql.add(where(property, ">", clazz + ".gt"));
            params.put(clazz + ".gt", gt);
        }

        if (lt != null) {
            hql.add(where(property, "<", clazz + ".lt"));
            params.put(clazz + ".lt", lt);
        }

        if (!Utils.isEmpty(in)) {
            hql.add(where(property, "in", clazz + ".in"));
            params.put(clazz + ".in", in);
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
