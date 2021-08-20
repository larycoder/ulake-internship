package org.usth.ict.ulake.folder.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserSearchQuery;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileRepository implements PanacheRepository<UserFile> {
    public List<UserFile> search(UserSearchQuery query) {
        String hql = "";
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        if (!Utils.isEmpty(query.keyword)) {
            conditions.add("name like :keyword");
            params.put("keyword", "%" + query.keyword + "%");
        }

        if (query.minSize > 0) {
            conditions.add("size > :minSize");
            params.put("minSize", query.minSize);
        }

        if (query.maxSize > 0) {
            conditions.add("size > :maxSize");
            params.put("maxSize", query.maxSize);
        }

        if (!Utils.isEmpty(query.mime)) {
            conditions.add("mime like :mime");
            params.put("mime", "%" + query.mime + "%");
        }
        hql = String.join(" and ", conditions);
        return list(hql, params);
    }
}
