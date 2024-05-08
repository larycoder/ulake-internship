package org.usth.ict.ulake.core.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.model.LakeObjectSearchQuery;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ObjectRepository implements PanacheRepository<LakeObject> {
    public List<LakeObject> search(LakeObjectSearchQuery query) {
        var conditions = new ArrayList<String>();
        var params = new HashMap<String, Object>();

        // cid
        if (!Utils.isEmpty(query.keyword)) {
            conditions.add("(cid like :keyword)");
            params.put("keyword", "%" + query.keyword + "%");
        }

        // accessTime
        if (query.minAccessTime != null && query.minAccessTime > 0) {
            conditions.add("(accessTime > :minAccessTime)");
            params.put("minAccessTime", query.minAccessTime);
        }

        if (query.maxAccessTime != null && query.maxAccessTime > 0) {
            conditions.add("(accessTime < :minAccessTime)");
            params.put("maxAccessTime", query.maxAccessTime);
        }

        // createTime
        if (query.minCreateTime != null && query.minCreateTime > 0) {
            conditions.add("(createTime > :minCreateTime)");
            params.put("minCreateTime", query.minCreateTime);
        }

        if (query.maxCreateTime != null && query.maxCreateTime > 0) {
            conditions.add("(createTime < :maxCreateTime)");
            params.put("maxCreateTime", query.maxCreateTime);
        }

        // query
        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }
}
