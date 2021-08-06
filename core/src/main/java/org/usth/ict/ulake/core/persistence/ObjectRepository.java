package org.usth.ict.ulake.core.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.core.model.LakeObject;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ObjectRepository implements PanacheRepository<LakeObject> {

}
