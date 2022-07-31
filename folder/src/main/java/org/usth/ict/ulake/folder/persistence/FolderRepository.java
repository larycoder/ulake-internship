package org.usth.ict.ulake.folder.persistence;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.StatsByDate;
import org.usth.ict.ulake.common.model.folder.UserFolderSearchQuery;
import org.usth.ict.ulake.folder.model.UserFolder;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FolderRepository implements PanacheRepository<UserFolder> {
    @Inject EntityManager em;

    public List<UserFolder> load(List<UserFolder> detach) {
        UserFolder attach;

        if (detach == null || detach.isEmpty())
            return null;

        var result = new ArrayList<UserFolder>();
        for (var file : detach) {
            if (file.id != null) {
                attach = findById(file.id);
                if (attach != null) result.add(attach);
            }
        }

        return result;
    }

    public List<UserFolder> listRoot(Long ownerId) {
        return list("(parent = NULL) AND (ownerId = ?1)", ownerId);
    }

    public List<UserFolder> listRoot() {
        return list("parent = NULL");
    }

    public List<StatsByDate> getNewFoldersByDate() {
        List<Object[]> counts = em.createNativeQuery("SELECT count(name) as count, DATE(FROM_UNIXTIME(`creationTime`)) as date FROM UserFolder GROUP BY date;").getResultList();
        List<StatsByDate> ret = new ArrayList<>();
        for (var count: counts) {
            StatsByDate stat = new StatsByDate((Date) count[1], ((BigInteger) count[0]).intValue());
            ret.add(stat);
        }
        return ret;
    }

    public List<UserFolder> search(UserFolderSearchQuery query) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (query.ids != null && !query.ids.isEmpty()) {
            var ids = new ArrayList<Long>();
            for (var id : query.ids) {
                if (id >= 0) ids.add(id);
            }
            conditions.add("(id in (:ids))");
            params.put("ids", ids);
        }

        if (query.ownerIds != null && !query.ownerIds.isEmpty()) {
            var ownerIds = new ArrayList<Long>();
            for (var ownerId : query.ownerIds) {
                if (ownerId >= 0)
                    ownerIds.add(ownerId);
            }
            conditions.add("(ownerId in (:ownerIds))");
            params.put("ownerIds", ownerIds);
        }

        if (!Utils.isEmpty(query.keyword)) {
            conditions.add("(name like :keyword)");
            params.put("keyword", "%" + query.keyword + "%");
        }

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

}
