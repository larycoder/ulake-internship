package org.usth.ict.ulake.user.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserSearchQuery;
import org.usth.ict.ulake.user.resource.AuthResource;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    @Inject
    AuthResource authResource;

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

        if (query.keywords != null && !query.keywords.isEmpty()) {
            for (var keyword : query.keywords)
                if (!Utils.isEmpty(keyword)) {
                    conditions.add("(userName like :keyword or firstName like :keyword or lastName like :keyword or email like :keyword)");
                    params.put("keyword", "%" + keyword + "%");
                }
        }

        if (query.minRegisterTime > 0) {
            conditions.add("(registerTime >= :minRegisterTime)");
            params.put("minRegisterTime", query.minRegisterTime);
        }

        if (query.maxRegisterTime > 0) {
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
}
