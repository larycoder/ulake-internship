package org.usth.ict.ulake.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import java.util.List;

@RestController
public class GroupRest {
    private static final Logger log = LoggerFactory.getLogger(GroupRest.class);

    @Autowired
    private List<FileSystem> fs;

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
        fs.get(0).ls(String.valueOf(id));
        return repository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    @GetMapping("/group/list/{path}")
    LakeGroup listByPath(@PathVariable String path) {
        log.info("{}: {}", path, fs.get(0).ls(path));
        return null;
    }

    private class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(Integer id) {
            super("Could not find group " + id);
        }
    }
}
