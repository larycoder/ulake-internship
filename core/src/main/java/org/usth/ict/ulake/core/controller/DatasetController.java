package org.usth.ict.ulake.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.usth.ict.ulake.core.model.LakeDataset;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.persistence.DatasetRepository;

import java.util.List;

@RestController
public class DatasetController {
    private static final Logger log = LoggerFactory.getLogger(DatasetController.class);

    private final DatasetRepository repository;

    public DatasetController(DatasetRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/dataset")
    List<LakeDataset> all() {
        return repository.findAll();
    }
}
