package org.usth.ict.ingest.crawler.storage;

import org.usth.ict.ingest.models.CrawlInfoEntity;
import org.usth.ict.ingest.models.NameSpaceEntity;
import org.usth.ict.ingest.models.QueueEntity;
import org.usth.ict.ingest.models.macro.Carrier;
import org.usth.ict.ingest.models.macro.Meta;
import org.usth.ict.ingest.persistence.QueueRepo;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * storage interacts to Queue table
 * current excepted meta:
 *  1. base_info:
 *      - name_id: namespace id (int)
 *      - crawl_id: crawl-info id (int)
 *  2. action: int
 *      - ADD: add link (store method)
 *      - CHECK: check link status (get method)
 *      - INFO: get link full info (get method)
 *      - UPDATE: update link status (store method)
 *  3. status: define link status in queue (int)
 *
 * =====================================================
 * carrier - holding data information
 * carrier supported type: Map
 * meta structure:
 * 1. link: String (searching info - option)
 * 2. action: int (following above meta)
 * 3. status: String (following above meta)
 * 4. carrier: Map (carry stored info), hold 2 type holder:
 *      1. data: List<Map> (carry info)
 *          - link: String (name of link - INFO)
 *          - extra: String (extra info of link - INFO)
 *          - status: String (link crawl status - INFO)
 *      2. checker: Boolean (carry checker result - CHECK)
 *
 * NOTE:
 * if *link info* is empty, storage return info of all
 * link in namespace with crawl_id defined in config.
 * (only work with INFO action)
 *
 */
public class LinkStorageImpl implements Storage {
    private Long name_id;
    private Long crawl_id;

    @Inject
    QueueRepo rp;

    @Override
    public void setup(HashMap config) {
       name_id = (long) config.get(Meta.NAME_ID);
       crawl_id = (long) config.get(Meta.CRAWL_ID);
    }

    @Override
    public void store(Object data, HashMap meta) {
        String link = (String) data;
        int action = (int) meta.get(Meta.ACTION);
        String status = (String) meta.get(Meta.STATUS);

        QueueEntity newRecord = new QueueEntity();
        newRecord.setName(new NameSpaceEntity(name_id));
        newRecord.setInfo(new CrawlInfoEntity(crawl_id));
        newRecord.setLink(link);
        newRecord.setStatus(status);

        if(action == Meta.ADD){
            rp.addRecord(newRecord);
        } else if(action == Meta.UPDATE) {
            QueueEntity searchInfo = new QueueEntity();
            searchInfo.setName(newRecord.getName());
            searchInfo.setInfo(newRecord.getInfo());
            searchInfo.setLink(newRecord.getLink());
            List<QueueEntity> rcList = rp.search(searchInfo);
            for(QueueEntity rc : rcList) {
                rp.updateRecord(rc.getId(), newRecord);
            }
        }
    }

    private List<Map> buildHolder(List<QueueEntity> rcs) {
        List<Map> holder = new ArrayList<>();
        if(rcs == null) return holder;
        for(QueueEntity rc : rcs){
            HashMap item = new HashMap();
            item.put(Carrier.LINK, rc.getLink());
            item.put(Carrier.EXTRA, rc.getInfo().getExtra());
            item.put(Carrier.STATUS, rc.getStatus());
            holder.add(item);
        }
        return holder;
    }

    @Override
    public void get(Object carrier, HashMap meta) {
        int action = (int) meta.get(Meta.ACTION);
        String status = (String) meta.get(Meta.STATUS);
        String searchLink = (String) meta.get(Meta.LINK);
        Map carrierMap = (Map) carrier;
        carrierMap.clear();

        QueueEntity checker = new QueueEntity();
        checker.setName(new NameSpaceEntity(this.name_id));
        checker.setInfo(new CrawlInfoEntity(this.crawl_id));

        if(action == Meta.INFO){
            checker.setLink(searchLink);
            List<QueueEntity> result = rp.search(checker);
            carrierMap.put(Carrier.DATA, buildHolder(result));
        } else if(action == Meta.CHECK){
            if(searchLink == null) searchLink = "";
            checker.setLink(searchLink);
            checker.setStatus(status);
            List<QueueEntity> result = rp.search(checker);
            carrierMap.put(Carrier.CHECKER, !result.isEmpty());
        }
    }
}
