package org.usth.ict.ulake.user.persistence;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.StatsByDate;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserSearchQuery;
import org.usth.ict.ulake.user.resource.AuthResource;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    @Inject
    AuthResource authResource;

    @Inject EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    public User checkLogin(LoginCredential cred, boolean skipPassword) {
        User user = find("userName", cred.getUserName()).firstResult();
        if (user == null) return null;
        if (skipPassword) return user;
        try {
            if (authResource.verifyPassword(user.password, cred.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            log.info(e.toString());
            return null;
        }
    }

    public User checkRefreshLogin(LoginCredential cred) {
        return find("refreshToken", cred.getRefreshToken()).firstResult();
    }

    public List<User> search(UserSearchQuery query) {
        ArrayList<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if(query.ids != null && !query.ids.isEmpty()) {
            conditions.add("(id in (:ids))");
            params.put("ids", query.ids);
        }

        // TODO: Bug in filtering multiple keywords
        if (query.keywords != null && !query.keywords.isEmpty()) {
            for (var keyword : query.keywords)
                if (!Utils.isEmpty(keyword)) {
                    conditions.add("(userName like :keyword or firstName like :keyword or lastName like :keyword or email like :keyword)");
                    params.put("keyword", "%" + keyword + "%");
                }
        }

        if (query.minRegisterTime != null && query.minRegisterTime > 0) {
            conditions.add("(registerTime >= :minRegisterTime)");
            params.put("minRegisterTime", query.minRegisterTime);
        }

        if (query.maxRegisterTime != null && query.maxRegisterTime > 0) {
            conditions.add("(registerTime <= :maxRegisterTime)");
            params.put("maxRegisterTime", query.maxRegisterTime);
        }


        if (query.groups != null && !query.groups.isEmpty()) {
            conditions.add("(groups.id in :groupIds)");
            params.put("groupIds", query.groups);
        }

        if (query.departments != null && !query.departments.isEmpty()) {
            conditions.add("(group.id in :departmentIds)");
            params.put("departmentIds", query.departments);
        }

        String hql = String.join(" and ", conditions);
        return list(hql, params);
    }


    public List<StatsByDate> getUserRegistrationByDate() {
        List<Object[]> counts = em.createNativeQuery("SELECT count(userName) as count, DATE(FROM_UNIXTIME(`registerTime`)) as date FROM User GROUP BY date;").getResultList();
        List<StatsByDate> ret = new ArrayList<>();
        for (var count: counts) {
            StatsByDate stat = new StatsByDate((Date) count[1], ((BigInteger) count[0]).intValue());
            ret.add(stat);
        }
        return ret;
    }

}
