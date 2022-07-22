package org.usth.ict.ulake.ingest.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.model.CrawlRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrawlRequestRepo implements PanacheRepository<CrawlRequest> {
}
