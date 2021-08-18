package org.usth.ict.ulake.user.persistence;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.resource.AuthResource;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    public User checkLogin(LoginCredential cred) {
        log.info("Checking login for creds {}, {}", cred.getUserName(), cred.getPassword());
        return find("userName = ?1 and password = ?2",
                cred.getUserName(),
                BcryptUtil.bcryptHash(cred.getPassword())).firstResult();
    }
}
