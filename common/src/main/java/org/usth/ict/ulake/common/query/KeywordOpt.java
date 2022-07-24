package org.usth.ict.ulake.common.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usth.ict.ulake.common.misc.Utils;

public class KeywordOpt extends Operation<Object> {
    public List<String> values;
    public List<String> fields;

    @Override
    public HqlResult getHQL(String property) {
        String clazz = this.getClass().getName().replaceAll("\\.", "_");
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // check value in each field for all values
        if (!Utils.isEmpty(fields)) {
            for (String value : values) {
                List<String> subHql = new ArrayList<>();
                for (String field : fields) {
                    subHql.add(where(field, "like", clazz + "_field_" + field));
                    params.put(clazz + "_field_" + field, "%" + value + "%");
                }
                hql.add("(" + String.join(" OR ", subHql) + ")");
            }
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
