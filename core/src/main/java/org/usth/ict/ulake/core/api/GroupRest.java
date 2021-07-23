package org.usth.ict.ulake.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import java.util.List;

@RestController
public class GroupRest {
    @Autowired
    private ApplicationContext context;

    private final GroupRepository repository;

    public GroupRest(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/group")
    List<LakeGroup> all() {
        return repository.findAll();
    }

    @GetMapping("/group/{id}")
    LakeGroup one(@PathVariable Integer id) {
        OpenIO oio = context.getBean(OpenIO.class);
        System.out.println("Created " + id + " with cid " + oio.mkdir(String.valueOf(id)));
        return repository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    private class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(Integer id) {
            super("Could not find group " + id);
        }
    }
}
