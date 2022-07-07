package org.usth.ict.ulake.common.misc;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    protected static final Logger log = LoggerFactory.getLogger(Utils.class);

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

    public static <T> T parseLakeResp(LakeHttpResponse resp, Class<T> clazz) {
        if (resp.getCode() != 200) {
            log.error("Error in response: {}", resp.getCode());
            return null;
        }
        if (!(resp.getResp() instanceof Map)) {
            log.error("Error parsing response", resp.getResp());
            return null;
        }
        final var map = (Map<String, Object>) resp.getResp();
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(map, clazz);
    }
}
