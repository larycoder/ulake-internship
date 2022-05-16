package org.usth.ict.ulake.dashboard.filter.impl.op;

import java.util.Map;

import org.usth.ict.ulake.dashboard.filter.Operator;
import org.usth.ict.ulake.dashboard.filter.QueryException;

public class Equal implements Operator<Map<String, Object>, String, Object> {
    @Override
    public Boolean verify(
        Map<String, Object> data, String property, Object value
    ) throws QueryException {
        var prop = data.get(property);
        if (prop == null) {
            return false;
        } else if (prop instanceof String) {
            return ((String) prop).equals(value);
        } else if (prop instanceof Long) {
            return ((Long) prop).equals(Long.parseLong(value.toString()));
        } else if (prop instanceof Integer) {
            return ((Integer) prop).equals(Integer.parseInt(value.toString()));
        } else if (prop instanceof Float) {
            return ((Float) prop).equals(Float.parseFloat(value.toString()));
        } else {
            throw new QueryException("Data type is not supported");
        }
    }
}
