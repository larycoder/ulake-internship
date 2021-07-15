package org.usth.ict.ulake.core.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.model.StorageObject;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

@RestController
public class ObjectRest {
    private final ObjectRepository repository;

    public ObjectRest(ObjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/object/{id}")
    StorageObject one(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
    }

    private class ObjectNotFoundException extends RuntimeException {
        public ObjectNotFoundException(Integer id) {
            super("Could not find object " + id);
        }
    }
}
