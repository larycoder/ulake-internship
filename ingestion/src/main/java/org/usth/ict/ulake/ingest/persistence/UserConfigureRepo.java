package org.usth.ict.ulake.ingest.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.model.UserConfigure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserConfigureRepo implements PanacheRepository<UserConfigure> {
}
