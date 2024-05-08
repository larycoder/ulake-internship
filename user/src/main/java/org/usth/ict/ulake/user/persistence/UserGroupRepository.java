package org.usth.ict.ulake.user.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.user.model.UserGroup;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserGroupRepository implements PanacheRepository<UserGroup> {
}
