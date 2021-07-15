package org.usth.ict.ulake.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.usth.ict.ulake.core.model.GroupObject;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(GroupRepository repository) {
        return args -> {
            //log.info("Preloading " + repository.save(new GroupObject("Whatevar.")));
        };
    }
}
