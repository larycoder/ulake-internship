package org.usth.ict.ulake.common.query;

import java.util.Map;

public class HqlResult {
    public String hql;
    public Map<String, Object> params;

    public HqlResult() {}

    public HqlResult(String hql, Map<String, Object> params) {
        this.hql = hql;
        this.params = params;
    }
}
