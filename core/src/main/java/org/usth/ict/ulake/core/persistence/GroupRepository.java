package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.usth.ict.ulake.core.model.LakeGroup;

public interface GroupRepository extends JpaRepository<LakeGroup, Long> {
}
