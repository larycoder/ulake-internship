package org.usth.ict.ulake.common.model.core;

import java.util.List;

public class GroupObjectModel {
    public Long id;
    public String name;
    public GroupObjectModel group;
    public List<ObjectModel> objects;
    public List<GroupObjectModel> groups;

    public GroupObjectModel() {}

    public GroupObjectModel(
        Long id, String name, GroupObjectModel group,
        List<ObjectModel> objects, List<GroupObjectModel> groups) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.objects = objects;
        this.groups = groups;
    }
}
