package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.usth.ict.ulake.core.model.LakeObject;

public interface ObjectRepository extends JpaRepository<LakeObject, Integer> {
}
