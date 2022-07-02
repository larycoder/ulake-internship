package org.usth.ict.ulake.ingest.services;

import org.usth.ict.ulake.ingest.model.DataModel;
import org.usth.ict.ulake.ingest.model.NameSpaceEntity;
import org.usth.ict.ulake.ingest.persistence.NameSpaceRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NameSpaceSvc {
    @Inject
    NameSpaceRepo rp;

    public DataModel listAll(){
        List<NameSpaceEntity> list = rp.listAll();
        return new DataModel(list);
    }

    public DataModel info(int recordId){
        List<NameSpaceEntity> list = new ArrayList<>();
        list.add(rp.findById((long) recordId));
        return new DataModel(list);
    }

    public DataModel addRecord(String name){
        NameSpaceEntity newRecord = new NameSpaceEntity();
        newRecord.setName(name);
        if(rp.insertData(newRecord) == true){
            List newList = new ArrayList<NameSpaceEntity>();
            newList.add(newRecord);
            return new DataModel(newList);
        } else{
            return new DataModel(new ArrayList());
        }
    }

    public DataModel deleteById(long id){
        rp.deleteById(id);
        return new DataModel();
    }
}
