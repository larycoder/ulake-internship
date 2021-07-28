package org.usth.ict.ulake.core.controller;

import com.google.gson.Gson;
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
    public String post(@ModelAttribute LakeObjectFormWrapper object) throws IOException {
        String metadata = object.getMetadata();
        log.info("POST: Prepare to create object with meta {}", metadata);
        LakeObjectMetadata meta = gson.fromJson(metadata, LakeObjectMetadata.class);
        InputStream is = object.getFile().getInputStream();
        String cid = fs.get(0).create(is);
        log.info("POST: object storage returned cid={}", cid);
        return LakeHttpResponse.toString(200);
    }

    private class ObjectNotFoundException extends RuntimeException {
        public ObjectNotFoundException(Integer id) {
            super("Could not find object " + id);
        }
    }
}
