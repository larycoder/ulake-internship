package org.usth.ict.ulake.dashboard.filter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.FilterService;
import org.usth.ict.ulake.dashboard.filter.Operator;
import org.usth.ict.ulake.dashboard.filter.QueryException;

public class FilterServiceImpl<T>
    implements FilterService<List<T>, FilterModel, List<T>> {

    @Override
    @SuppressWarnings("unchecked")
    public List<T> filter(List<T> data, FilterModel filter) throws QueryException {
        List<T> rst = new ArrayList<T>();

        var op = (Operator<Map<String, Object>, String, Object>) filter.op;
        var prop = (String) filter.property;
        var value = (Object) filter.value;

        var mapper = new ObjectMapper();
        var typeRef = new TypeReference<List<Map<String, Object>>>() {};
        var dataAsMap = mapper.convertValue(data, typeRef);

        for (int i = 0; i < dataAsMap.size(); i++) {
            if (op.verify(dataAsMap.get(i), prop, value)) {
                rst.add(data.get(i));
            }
        }
        return rst;
    }
}
