package org.usth.ict.ulake.core.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.misc.Utils;
import org.usth.ict.ulake.core.model.*;
import org.usth.ict.ulake.core.persistence.GroupRepository;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@RestController
public class ObjectController {
    private static final Logger log = LoggerFactory.getLogger(ObjectController.class);
    private final ObjectRepository repository;
    private final GroupRepository groupRepository;
    private final Gson gson = new Gson();

    @Autowired
    private List<FileSystem> fs;

    public ObjectController(ObjectRepository repository, GroupRepository groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    @GetMapping("/object")
    public List<LakeObject> all() {
        return repository.findAll();
    }

    @GetMapping("/object/{cid}")
    public LakeObject one(@PathVariable String cid) {
        return repository.findByCid(cid);
    }

    @GetMapping("/object/data/{cid}")
    public void data(HttpServletResponse response,
                     HttpServletRequest request,
                     @PathVariable String cid) {
        LakeObject object = repository.findByCid(cid);
        if (object == null) {
            response.setStatus(404);
            return;
        }
        InputStream is = fs.get(0).get(cid);
        if (is == null) {
            response.setStatus(404);
            return;
        }

        try {
            is.transferTo(response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(403);
        }
    }

    @PostMapping("/object")
    public String post(@ModelAttribute LakeObjectFormWrapper objectWrapper) throws IOException {
        // extract data from POSTed multi-part form
        InputStream is = objectWrapper.getFile().getInputStream();
        String metadata = objectWrapper.getMetadata();
        log.info("POST: Prepare to create object with meta {}", metadata);
        LakeObjectMetadata meta = gson.fromJson(metadata, LakeObjectMetadata.class);
        LakeGroup group = null;
        if (meta.getGroupId() != 0) {
            group = groupRepository.getById(meta.getGroupId());
        }

        // save to backend
        String cid = fs.get(0).create(meta.getName(), meta.getLength(), is);
        log.info("POST: object storage returned cid={}", cid);

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
        object.setGroup(group);
        repository.save(object);

        JsonElement element = gson.toJsonTree(object, LakeObject.class);
        return LakeHttpResponse.toString(200, null, element);
    }
}
