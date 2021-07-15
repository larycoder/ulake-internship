package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.usth.ict.ulake.core.model.GroupObject;

public interface GroupRepository extends JpaRepository<GroupObject, Integer> {
}
