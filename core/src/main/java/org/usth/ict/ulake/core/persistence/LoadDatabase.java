package org.usth.ict.ulake.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.usth.ict.ulake.core.model.Group;
import org.usth.ict.ulake.core.model.StorageObject;

import java.util.ArrayList;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(GroupRepository repository) {
        return args -> {
            ArrayList<StorageObject> objects = new ArrayList<>();
            objects.add(new StorageObject("this-is-a-cid"));
            Group g = new Group("Whatevar.");
            g.setObjects(objects);

            log.info("Preloading " + repository.save(g));
        };
    }
}
