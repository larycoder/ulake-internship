package org.usth.ict.ulake.search.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.common.model.folder.UserFileSearchQueryV2;
import org.usth.ict.ulake.common.model.user.UserSearchQueryV2;
import org.usth.ict.ulake.common.query.Queryable;

@ApplicationScoped
public class SearchParser {
    @Inject
    ObjectMapper mapper;

    /**
     * Lookup table for search query type.
     * Aware Behavior: query type must not have generic type
     * */
    private Map<String, Class<?>> lookup() {
        var table = new HashMap<String, Class<?>>();
        table.put("user", UserSearchQueryV2.class);
        table.put("file", UserFileSearchQueryV2.class);
        return table;
    }

    /**
     * Parse map of object to map of query.
     * Aware Behavior: property will be ignored in case of fail parsing.
     * */
    public Map<String, Queryable> parse(Map<String, Object> query) {
        Map<String, Class<?>> typeLookup = lookup();
        Map<String, Map<String, Object>> jsonQuery = new HashMap<>();

        // parse 1 level dot to 2 level of domain map
        for (var entry : query.entrySet()) {
            String[] key = entry.getKey().split("\\.", 2);
            if (jsonQuery.get(key[0]) == null)
                jsonQuery.put(key[0], new HashMap<String, Object>());
            jsonQuery.get(key[0]).put(key[1], entry.getValue());
        }

        // convert domain map to domain query
        var myQuery = new HashMap<String, Queryable>();
        for (var entry : jsonQuery.entrySet()) {
            var key = entry.getKey();
            var obj = jsonQuery.get(key);
            var clazz = typeLookup.get(key);
            myQuery.put(key, (Queryable) mapper.convertValue(obj, clazz));
        }

        return myQuery;
    }

    public Boolean isEmpty(Queryable query) {
        if (query == null)
            return true;

        for (Field f : query.getClass().getDeclaredFields()) {
            try {
                if (f.get(query) != null)
                    return false;
            } catch (IllegalAccessException e) {
                ; // Do nothing in case field is not accessible
            }
        }
        return true;
    }
}
