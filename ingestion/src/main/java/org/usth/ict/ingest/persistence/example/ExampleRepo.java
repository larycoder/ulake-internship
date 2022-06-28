package org.usth.ict.ingest.persistence.example;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class ExampleRepo {
    @Inject
    EntityManager em;

    @Transactional
    public void add(Object entity){
        em.persist(entity);
    }
}
