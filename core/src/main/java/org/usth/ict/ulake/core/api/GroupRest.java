package org.usth.ict.ulake.core.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.model.Group;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import java.util.List;

@RestController
public class GroupRest {
    private final GroupRepository repository;

    public GroupRest(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/group")
    List<Group> all() {
        return repository.findAll();
    }

    @GetMapping("/group/{id}")
    Group one(@PathVariable Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    private class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(Integer id) {
            super("Could not find group " + id);
        }
    }
}
