package org.usth.ict.ingest.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ingest.models.CrawlInfoEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CrawlInfoRepo implements PanacheRepository<CrawlInfoEntity> {
    // put custom logic here
    @Transactional
    public boolean insertData(CrawlInfoEntity rc){
        String hql = "name_id = ?1 AND link = ?2";
        List list = find(hql, rc.getName(), rc.getLink()).list();
        if(list.size() == 0){
            persist(rc);
            return true;
        }
        return false;
    }

    @Transactional
    public List<CrawlInfoEntity> searchByNameSpace(String name){
        String hql = "name.name = ?1";
        return find(hql, name).list();
    }

    @Transactional
    public void deleteById(long id){
        CrawlInfoEntity rc = findById(id);
        delete(rc);
    }
}
