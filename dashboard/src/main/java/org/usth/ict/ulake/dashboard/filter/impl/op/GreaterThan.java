package org.usth.ict.ulake.dashboard.filter.impl.op;

import java.util.Map;

import org.usth.ict.ulake.dashboard.filter.Operator;
import org.usth.ict.ulake.dashboard.filter.QueryException;

public class GreaterThan implements Operator<Map<String, Object>, String, Object> {
    @Override
    public Boolean verify(
        Map<String, Object> data, String property, Object value
    ) throws QueryException {
        var prop = data.get(property);

        if (prop instanceof Long) {
            return (Long) prop > Long.parseLong(value.toString());
        } else if (prop instanceof Integer && value instanceof Integer) {
            return (Integer) prop > Integer.parseInt(value.toString());
        } else {
            throw new QueryException("Data type is not supported");
        }
    }
}
