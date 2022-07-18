package org.usth.ict.ulake.extract.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.persistence.ExtractRequestRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultFileRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultRepository;

@ApplicationScoped
public class ExtractorBean {
    private static final Logger log = LoggerFactory.getLogger(ExtractorBean.class);

    public String token;

    @Inject
    ExtractRequestRepository repoReq;

    @Inject
    ExtractResultFileRepository repoResFile;

    @Inject
    ExtractResultRepository repoRes;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    @RestClient
    FileService fileService;

    @Inject
    @RestClient
    DashboardService dashboardService;

    @Inject
    ZipExtractor extractor;

    @Inject
    ExtractTask task;

    /**
     * Start extraction service in background with specified id
     * @param id Compression request Id
     */
    @Transactional
    public void extract(Long id) {
        log.info("Start extraction in managed bean");
        log.info("req count: {}", repoReq.count());
        log.info("req result count: {}", repoRes.count());
        log.info("req file result count: {}", repoResFile.count());
        task.requestId = id;
        //task.run();
    }
}
