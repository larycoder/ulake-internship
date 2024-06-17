package org.usth.ict.ulake.extract.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;

/**
 * General extractor interface
 */
@ApplicationScoped
public abstract class Extractor {
    protected static final Logger log = LoggerFactory.getLogger(Extractor.class);

    @Inject
    @RestClient
    protected CoreService coreService;

    @Inject
    @RestClient
    protected FileService fileService;

    @Inject
    @RestClient
    protected DashboardService dashboardService;

    public Extractor() {
    }

    public abstract void extract(String bearer, ExtractRequest request, ExtractResult result, ExtractCallback callback);
}

