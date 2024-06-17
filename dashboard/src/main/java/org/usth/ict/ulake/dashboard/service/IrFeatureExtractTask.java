package org.usth.ict.ulake.dashboard.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.service.IrService;
import org.usth.ict.ulake.common.task.ScheduledTask;

/**
 * Perform extraction in a background thread
 */
@ApplicationScoped
public class IrFeatureExtractTask extends ScheduledTask implements IrFeatureExtractCallback {
    private static final Logger log = LoggerFactory.getLogger(IrFeatureExtractTask.class);

    @Inject
    @RestClient
    protected IrService irService;

    public IrFeatureExtractTask() {
    }

    /**
     * Performs the image indexing task in current thread
     * @param id
     */
    public void run(String bearer, Long id) {
        Long resp = irService.extract(bearer, id).getResp();
        log.info("Extract task: irService returns {}", resp);
    }

    @Override
    public void callback(Long irId, boolean success) {
        log.info("  + ir service callback: irId {}, status {}, progress {}", irId, success);
    }
}
