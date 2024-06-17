package org.usth.ict.ulake.core.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.core.model.LakeGroup;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupRepository implements PanacheRepository<LakeGroup> {
}
