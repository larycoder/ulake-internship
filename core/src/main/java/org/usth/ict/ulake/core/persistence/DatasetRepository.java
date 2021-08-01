package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.usth.ict.ulake.core.model.LakeDataset;

public interface DatasetRepository extends JpaRepository<LakeDataset, Long> {
}
