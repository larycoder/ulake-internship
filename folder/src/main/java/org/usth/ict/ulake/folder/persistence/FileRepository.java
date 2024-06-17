package org.usth.ict.ulake.folder.persistence;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.StatsByDate;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQuery;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQueryV2;
import org.usth.ict.ulake.common.query.HqlResult;
import org.usth.ict.ulake.folder.model.UserFile;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FileRepository implements PanacheRepository<UserFile> {
    @Inject EntityManager em;

    public List<UserFile> load(List<UserFile> detach) {
        UserFile attach;

        if (detach == null || detach.isEmpty())
            return null;

        var result = new ArrayList<UserFile>();
        for (var file : detach) {
            if (file.id != null) {
                attach = findById(file.id);
                if (attach != null) result.add(attach);
            }
        }

        return result;
    }

    public List<UserFile> search(UserFileSearchQuery query) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (query.ids != null && !query.ids.isEmpty()) {
            var ids = new ArrayList<Long>();
            for (var id : query.ids) {
                if (id >= 0)
                    ids.add(id);
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

        if (query.minSize != null && query.minSize > 0) {
            conditions.add("(size > :minSize)");
            params.put("minSize", query.minSize);
        }

        if (query.maxSize != null && query.maxSize > 0) {
            conditions.add("(size > :maxSize)");
            params.put("maxSize", query.maxSize);
        }

        if (!Utils.isEmpty(query.mime)) {
            conditions.add("(mime like :mime)");
            params.put("mime", "%" + query.mime + "%");
        }
        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }

    public List<UserFile> searchV2(UserFileSearchQueryV2 query) {
        HqlResult myQuery = query.getHQL("");
        return list(myQuery.hql, myQuery.params);
    }

    public List<UserFile> listRoot(Long ownerId) {
        return list("(parent = NULL) AND (ownerId = ?1)", ownerId);
    }

    public List<UserFile> listRoot() {
        return list("parent = NULL");
    }

    public List<StatsByDate> getNewFilesByDate() {
        List<Object[]> counts = em.createNativeQuery("SELECT count(name) as count, DATE(FROM_UNIXTIME(`creationTime`)) as date FROM UserFile GROUP BY date;").getResultList();
        List<StatsByDate> ret = new ArrayList<>();
        for (var count : counts) {
            StatsByDate stat = new StatsByDate((Date) count[1], ((BigInteger) count[0]).intValue());
            ret.add(stat);
        }
        return ret;
    }

}
