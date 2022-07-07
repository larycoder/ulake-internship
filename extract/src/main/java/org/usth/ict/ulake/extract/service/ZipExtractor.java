package org.usth.ict.ulake.extract.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ZipExtractor extends Extractor {
    private ObjectMapper mapper = new ObjectMapper();

    public ZipExtractor(String token, CoreService coreService, FileService fileService, DashboardService dashboardService) {
        super(token, coreService, fileService, dashboardService);
    }

    @Override
    public void extract(ExtractRequest request, ExtractResult result, ExtractCallback callback) {
        FileModel fileModel = null;
        FolderModel parent = null;
        try {
            var info = dashboardService.fileInfo(request.fileId, token);
            fileModel = mapper.convertValue(info.getResp(), FileModel.class);
        }
        catch (LakeServiceException e) {
            result.progress = -1L;  // indicates an error
            return;
        }

        try {
            var info = dashboardService.folderInfo(token, request.folderId);
            parent = mapper.convertValue(info.getResp(), FolderModel.class);
        }
        catch (LakeServiceException e) {
            result.progress = -2L;  // indicates an error
            return;
        }

        log.info("   + ZIP file cid {}, name {}", fileModel.cid, fileModel.name);
        log.info("   + Preparing to fetch file id {} from core", fileModel.id);
        InputStream fis = coreService.objectDataByFileId(fileModel.id, token);
        ZipInputStream zis = new ZipInputStream(fis);
        try {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                log.info("Zip dir {}, entry {}", entry.isDirectory(), entry.getName());
                save(zis, entry, parent);
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
     * @param folderId  0 or null to get to user's root
     * @return
     * @throws IOException
     */
    private Object save(ZipInputStream zis, ZipEntry entry, FolderModel parent) throws IOException {
        // TODO: nested directory support
        if (entry.isDirectory()) {
            // make a new dir
            FolderModel folder = new FolderModel();
            folder.name = entry.getName();
            folder.parent = parent;
            var resp = dashboardService.newFolder(token, folder).getResp();
            return mapper.convertValue(resp, FolderModel.class);
        }
        else {
            FileModel file = new FileModel();
            file.name = entry.getName();
            file.parent = parent;
            file.size = entry.getSize();

            FileFormModel fileModel = new FileFormModel();
            fileModel.fileInfo = file;
            fileModel.is = zis;
            var resp = dashboardService.newFile(token, fileModel);
            return mapper.convertValue(resp, FolderModel.class);
        }
    }
}
