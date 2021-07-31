package org.usth.ict.ulake.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.usth.ict.ulake.core.model.LakeObject;

public interface ObjectRepository extends JpaRepository<LakeObject, Integer> {
    @Query("select o from LakeObject o where o.cid = ?1")
    LakeObject findByCid(String cid);
}
