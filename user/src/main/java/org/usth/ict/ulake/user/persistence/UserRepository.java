package org.usth.ict.ulake.user.persistence;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.resource.AuthResource;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    public User checkLogin(LoginCredential cred, boolean skipPassword) {
        User user = find("userName", cred.getUserName()).firstResult();
        if (user == null) return null;
        if (skipPassword) return user;
        try {
            if (Utils.verifyPassword(user.password, cred.getPassword())) {
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

}
