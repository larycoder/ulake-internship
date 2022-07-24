package org.usth.ict.ulake.common.model.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.query.HqlResult;
import org.usth.ict.ulake.common.query.KeywordOpt;
import org.usth.ict.ulake.common.query.NumberOpt;
import org.usth.ict.ulake.common.query.Queryable;

public class UserSearchQueryV2 implements Queryable {
    @Schema(description = "User id allowed")
    public NumberOpt<Long> id;

    @Schema(description = "List of keywords in user name or email or first name, last name")
    public KeywordOpt keywords;

    // TODO: need solution to join groups with user for querying (many-to-many)
    //@Schema(description = "Group id to search users from")
    //public NumberOpt<Integer> groups;

    @Schema(description = "List of department ids to search users from")
    public NumberOpt<Integer> departments;

    @Schema(description = "register time (epoch) of account creation")
    public NumberOpt<Long> registerTime;

    public UserSearchQueryV2() {}

    @Override
    public HqlResult getHQL(String property) {
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        HqlResult hr;

        if (id != null) {
            hr = id.getHQL("id");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (keywords != null) {
            hr = keywords.getHQL("keywords");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (departments != null) {
            hr = departments.getHQL("department_id");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (registerTime != null) {
            hr = registerTime.getHQL("registerTime");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
