package org.usth.ict.ulake.ingest.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.model.CrawlTemplate;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CrawlTemplateRepo implements PanacheRepository<CrawlTemplate> {
}
