package org.usth.ict.ulake.common.misc;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isEmpty(String str) {
        if (str != null && !str.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(List<?> lst) {
        return (lst == null || lst.isEmpty());
    }

    public static Object convert(Object obj, Class<?> type)
    throws IllegalArgumentException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, type);
    }
}
