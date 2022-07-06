package org.usth.ict.ulake.extract.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;

public class ZipExtractor extends Extractor {

    public ZipExtractor(String token, CoreService coreService, FileService fileService, DashboardService dashboardService) {
        super(token, coreService, fileService, dashboardService);
    }

    @Override
    public void extract(ExtractRequest request, ExtractResult result, ExtractCallback callback) {

    }

    /**
     * Extract a zip entry and push to a target folder using dashboard service
     * @param entry
     * @param folderId
     * @return
     * @throws IOException
     */
    private InputStream save(ZipEntry entry, Long folderId) throws IOException {
        InputStream ret = null;
        if (entry.isDirectory()) {
            // make a new dir

        }
        return ret;
    }
}
