package org.usth.ict.ulake.ingest.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ulake.ingest.model.NameSpaceEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class NameSpaceRepo implements PanacheRepository<NameSpaceEntity> {
    // put custom logic here
    @Transactional
    public List<NameSpaceEntity> searchByName(String name){
        return find("name = ?1", name).list();
    }

    @Transactional
    public Boolean insertData(NameSpaceEntity record){
        List list = find("name", record.getName()).list();
        if (list.size() == 0){
            persist(record);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteById(long id){
        NameSpaceEntity rc = findById(id);
        delete(rc);
    }
}
