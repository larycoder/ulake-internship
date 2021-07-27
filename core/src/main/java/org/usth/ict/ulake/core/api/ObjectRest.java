package org.usth.ict.ulake.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class ObjectRest {
    private final ObjectRepository repository;

    @Autowired
    private List<FileSystem> fs;

    public ObjectRest(ObjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/object/{id}")
    LakeObject one(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
    }

    @PostMapping("/object")
    public String post(HttpServletRequest request) throws IOException {
        // TODO: multi-form parts - metadata and binary
        /*ServletInputStream is = request.getInputStream();
        String cid = fs.get(0).create(is);*/

        // TODO: json output support
        return "{msg: 'ok'}";
    }

    private class ObjectNotFoundException extends RuntimeException {
        public ObjectNotFoundException(Integer id) {
            super("Could not find object " + id);
        }
    }
}
