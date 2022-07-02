package org.usth.ict.ulake.ingest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.usth.ict.ulake.ingest.model.CrawlInfoEntity;
import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.NameSpaceEntity;
import org.usth.ict.ulake.ingest.persistence.CrawlInfoRepo;
import org.usth.ict.ulake.ingest.persistence.NameSpaceRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CrawlInfoSvc {
    @Inject
    CrawlInfoRepo rp;

    public DataModel listAll(){
        List<CrawlInfoEntity> list = rp.listAll();
        return new DataModel(list);
    }

    public DataModel infoByNameSpace(String name){
        DataModel model = new DataModel();
        return new DataModel(rp.searchByNameSpace(name));
    }

    public DataModel infoById(Long id){
        List<CrawlInfoEntity> list = new ArrayList<>();
        CrawlInfoEntity entity = rp.findById(id);
        if(entity != null) list.add(rp.findById(id));
        return new DataModel(list);
    }

    public DataModel add(
            Long name_id, String link, Map extra, Map headers){

        NameSpaceEntity nsp = new NameSpaceEntity(name_id);

        CrawlInfoEntity ci = new CrawlInfoEntity();
        ci.setLink(link);
        ci.setName(nsp);
        ci.setExtra(extra);
        ci.setHeaders(headers);

        if(rp.insertData(ci)){
            return new DataModel().insert(ci);
        }
        return new DataModel();
    }

    public DataModel deleteById(long id){
        rp.deleteById(id);
        return new DataModel();
    }
}
