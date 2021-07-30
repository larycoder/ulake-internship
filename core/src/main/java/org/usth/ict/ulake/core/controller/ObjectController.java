package org.usth.ict.ulake.core.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.misc.Utils;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.model.LakeObjectFormWrapper;
import org.usth.ict.ulake.core.model.LakeObjectMetadata;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@RestController
public class ObjectController {
    private static final Logger log = LoggerFactory.getLogger(ObjectController.class);
    private final ObjectRepository repository;
    private Gson gson = new Gson();

    @Autowired
    private List<FileSystem> fs;

    public ObjectController(ObjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/object/{id}")
    LakeObject one(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
    }

    @PostMapping("/object")
    public String post(@ModelAttribute LakeObjectFormWrapper objectWrapper) throws IOException {
        // extract data from POSTed multi-part form
        String metadata = objectWrapper.getMetadata();
        log.info("POST: Prepare to create object with meta {}", metadata);
        LakeObjectMetadata meta = gson.fromJson(metadata, LakeObjectMetadata.class);
        InputStream is = objectWrapper.getFile().getInputStream();

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
        object.setGroup(null);
        repository.save(object);


        JsonElement element = gson.toJsonTree(object, LakeObject.class);
        return LakeHttpResponse.toString(200, null, element);
    }

    private class ObjectNotFoundException extends RuntimeException {
        public ObjectNotFoundException(Integer id) {
            super("Could not find object " + id);
        }
    }
}
