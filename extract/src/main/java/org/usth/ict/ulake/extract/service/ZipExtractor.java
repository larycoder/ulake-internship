package org.usth.ict.ulake.extract.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;
import org.usth.ict.ulake.extract.model.ExtractResultFile;

@ApplicationScoped
public class ZipExtractor extends Extractor {
    private static final Logger log = LoggerFactory.getLogger(ExtractTask.class);

    public ZipExtractor() {
        super();
    }

    @Override
    public void extract(String bearer, ExtractRequest request, ExtractResult result, ExtractCallback callback) {
        log.info("{} starting extract for id {}", this.getClass().getName(), request.id);
        FileModel fileModel = (FileModel) dashboardService.fileInfo(request.fileId.toString(), bearer).getResp();
        FolderModel parent = dashboardService.folderInfo(bearer, request.folderId.toString()).getResp();

        // everything is done on streams, so no local file
        InputStream fis = coreService.objectDataByFileId(fileModel.id, bearer);
        ZipInputStream zis = new ZipInputStream(fis);
        try {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                log.info("Zip dir {}, entry {}", entry.isDirectory(), entry.getName());
                try {
                    Object ret = save(bearer, zis, entry, parent);
                    if (ret instanceof FileModel) {
                        FileModel extractedFileModel = (FileModel) ret;
                        ExtractResultFile resultFile = new ExtractResultFile();
                        resultFile.fileId = extractedFileModel.id;
                    }
                } catch (LakeServiceException e) {
                }
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
    private Object save(String bearer, ZipInputStream zis, ZipEntry entry, FolderModel parent) throws LakeServiceException {
        // TODO: nested directory support
        if (entry.isDirectory()) {
            // make a new dir
            FolderModel folder = new FolderModel();
            folder.name = entry.getName();
            folder.parent = parent;
            return dashboardService.newFolder(bearer, folder).getResp();
        }
        else {
            FileModel file = new FileModel();
            file.name = entry.getName();
            file.parent = parent;
            file.size = entry.getSize();

            FileFormModel fileModel = new FileFormModel();
            fileModel.fileInfo = file;
            fileModel.is = zis;
            return dashboardService.newFile(bearer, fileModel).getResp();
        }
    }
}
