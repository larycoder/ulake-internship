package org.usth.ict.ulake.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeObject;

import java.util.ArrayList;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(GroupRepository repository) {
        return args -> {
            /*ArrayList<LakeObject> objects = new ArrayList<>();
            objects.add(new LakeObject("this-is-a-cid"));
            LakeGroup g = new LakeGroup("Whatevar.");
            log.info("Preloading " + repository.save(g));*/
        };
    }
}
