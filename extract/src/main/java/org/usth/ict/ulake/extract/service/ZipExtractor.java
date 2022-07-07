package org.usth.ict.ulake.extract.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
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
        var fileInfo = dashboardService.fileInfo(request.fileId, token);
        FileModel fileModel = Utils.parseLakeResp(fileInfo, FileModel.class);
        if (fileModel == null) {
            result.requestId = -1L;  // indicates an error
            return;
        }

        var folderInfo = dashboardService.folderInfo(token, request.folderId);
        FolderModel parent = Utils.parseLakeResp(folderInfo, FolderModel.class);
        if (parent == null) {
            result.requestId = -2L;  // indicates an error
            return;
        }

        log.info("   + ZIP file cid {}, name {}", fileModel.cid, fileModel.name);
        log.info("   + Preparing to fetch file id {} from core", fileModel.id);
        InputStream fis = coreService.objectDataByFileId(fileModel.id, token);
        ZipInputStream zis = new ZipInputStream(fis);
        try {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                //save(entry, parent);
                log.info("Zip dir {}, entry {}", entry.isDirectory(), entry.getName());
                entry = zis.getNextEntry();
             }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            log.error("   + Error reading zip entry {}", e.getMessage());
            result.requestId = -1L;  // indicates an error
        }
    }

    /**
     * Extract a zip entry and push to a target folder using dashboard service
     * @param entry
     * @param folderId
     * @return
     * @throws IOException
     */
    private InputStream save(ZipEntry entry, FolderModel parent) throws IOException {
        InputStream ret = null;
        if (entry.isDirectory()) {
            // make a new dir
            FolderModel folder = new FolderModel();
            folder.name = entry.getName();
            folder.parent = parent;
            dashboardService.newFolder(token, folder);
        }
        else {
            FileModel file = new FileModel();
            file.name = entry.getName();
            file.parent = parent;
            file.size = entry.getSize();
        }
        return ret;
    }
}
