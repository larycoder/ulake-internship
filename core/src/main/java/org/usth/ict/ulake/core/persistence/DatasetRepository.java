package org.usth.ict.ulake.core.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.core.model.LakeDataset;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatasetRepository implements PanacheRepository<LakeDataset> {
}
