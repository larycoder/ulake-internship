package org.usth.ict.ulake.common.model.core;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ObjectModel {
    public Long id;
    public Long parentId;
    public Long accessTime;
    public Long createTime;
    public String cid;

    public ObjectModel() {}

    public ObjectModel(
        Long id, Long parentId, Long accessTime, Long createTime, String cid
    ) {
        this.id = id;
        this.parentId = parentId;
        this.accessTime = accessTime;
        this.createTime = createTime;
        this.cid = cid;
    }
}
