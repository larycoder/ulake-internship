package org.usth.ict.ulake.common.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.common.misc.Utils;

public class NumberOpt<T extends Number> extends Operation<T> {
    public T min;
    public T max;

    @Override
    public HqlResult getHQL(String property) {
        String clazz = this.getClass().getName();
        return getHQL(clazz, property);
    }

    protected HqlResult getHQL(String clazz, String property) {
        HqlResult result = super.getHQL(clazz, property);
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (!Utils.isEmpty(result.hql)) {
            hql.add("(" + result.hql + ")");
            params.putAll(result.params);
        }

        if (min != null) {
            hql.add(where(property, ">=", clazz + ".min"));
            params.put(clazz + ".min", min);
        }

        if (max != null) {
            hql.add(where(property, "<=", clazz + ".max"));
            params.put(clazz + ".max", max);
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
