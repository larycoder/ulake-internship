package org.usth.ict.ulake.common.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.common.misc.Utils;

public class StringOpt extends Operation<String> {
    public String like;

    @Override
    public HqlResult getHQL(String property) {
        String clazz = this.getClass().getName().replaceAll("\\.", "_");
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

        if (!Utils.isEmpty(like)) {
            hql.add(where(property, "like", clazz + "_like"));
            params.put(clazz + "_like", "%" + like + "%");
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
