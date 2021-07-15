package org.usth.ict.ulake.core.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.model.GroupObject;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import java.util.List;

@RestController
public class GroupRest {
    private final GroupRepository repository;

    public GroupRest(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/group")
    List<GroupObject> all() {
        return repository.findAll();
    }
}
