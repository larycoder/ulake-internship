package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.usth.ict.ulake.core.model.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
