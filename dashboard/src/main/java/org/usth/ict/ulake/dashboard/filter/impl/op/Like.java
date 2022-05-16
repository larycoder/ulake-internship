package org.usth.ict.ulake.dashboard.filter.impl.op;

import java.util.Map;

import org.usth.ict.ulake.dashboard.filter.Operator;
import org.usth.ict.ulake.dashboard.filter.QueryException;

public class Like implements Operator<Map<String, Object>, String, Object> {
    @Override
    public Boolean verify(
        Map<String, Object> data, String property, Object value
    ) throws QueryException {
        var prop = data.get(property);

        if (prop == null) {
            return false;
        } else if (prop instanceof String) {
            return ((String) prop).matches(value.toString());
        } else {
            throw new QueryException("data type is not supported");
        }
    }
}
