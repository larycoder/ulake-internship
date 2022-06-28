package org.usth.ict.ingest.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.usth.ict.ingest.models.QueueEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class QueueRepo implements PanacheRepository<QueueEntity> {
    @Inject
    NameSpaceRepo namespaceRepo;
    @Inject
    CrawlInfoRepo crawlinfoRepo;

    // put custom logic here
    private Map parseInfo(QueueEntity rc){
        Map map = new HashMap();
        if(rc.getId() != null) map.put("id", rc.getId());
        if(rc.getName() != null) map.put("name", rc.getName());
        if(rc.getInfo() != null) map.put("info", rc.getInfo());
        if(rc.getLink() != null) map.put("link", rc.getLink());
        if(rc.getStatus() != null) map.put("status", rc.getStatus());
        return map;
    }

    private QueueEntity syncInfo(QueueEntity dest, QueueEntity src){
        if(src.getId() != null) dest.setId(src.getId());
        if(src.getName() != null) dest.setName(src.getName());
        if(src.getInfo() != null) dest.setInfo(src.getInfo());
        if(src.getLink() != null) dest.setLink(src.getLink());
        if(src.getStatus() != null) dest.setStatus(src.getStatus());
        return dest;
    }

    @Transactional
    public List<QueueEntity> search(QueueEntity cond){
        Map<String, Object> infoMap = parseInfo(cond);
        String hql = "1=1 ";
        for(String key : infoMap.keySet()){
            hql += "AND " + key + " = :" + key + " ";
        }
        return find(hql, infoMap).list();
    }

    @Transactional
    public boolean addRecord(QueueEntity record) {
        List<QueueEntity> records = search(record);
        if(records.size() == 0) {
            record.setName(namespaceRepo.findById(record.getName().getId()));
            record.setInfo(crawlinfoRepo.findById(record.getInfo().getId()));
            persist(record);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateRecord(Long id, QueueEntity record) {
        QueueEntity persistedRecord = findById(id);
        if(persistedRecord != null) {
            syncInfo(persistedRecord, record);
            persist(persistedRecord);
            return true;
        }
        return false;
    }
}
