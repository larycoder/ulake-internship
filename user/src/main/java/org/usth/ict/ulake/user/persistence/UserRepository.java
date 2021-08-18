package org.usth.ict.ulake.user.persistence;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User checkLogin(LoginCredential cred) {
        cred.setPassword(BcryptUtil.bcryptHash(cred.getPassword()));
        return find("userName= ?1 and password = ?2", cred.getUsername(), cred.getPassword()).firstResult();
    }
}
