package org.usth.ict.ulake.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.GenericDAO;

import javax.inject.Inject;
import java.util.List;

@RestController
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    @Inject
    List<FileSystem> fs;

    @Inject
    GenericDAO<LakeGroup> groupDao;

    public GroupController(List<FileSystem> fs, GenericDAO<LakeGroup> groupDao) {
        this.fs = fs;
        this.groupDao = groupDao;
        this.groupDao.setClazz(LakeGroup.class);
    }

    @GetMapping("/group")
    List<LakeGroup> all() {
        return groupDao.list();
    }

    @GetMapping("/group/{id}")
    LakeGroup one(@PathVariable Long id) {
        fs.get(0).ls(String.valueOf(id));
        return groupDao.findById(id);
    }

    @PostMapping("/group")
    public String post() {
        return LakeHttpResponse.toString(200);
    }

    @GetMapping("/group/list/{path}")
    LakeGroup listByPath(@PathVariable String path) {
        log.info("{}: {}", path, fs.get(0).ls(path));
        return null;
    }
}
