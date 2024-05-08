package org.usth.ict.ulake.compress.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.compress.model.CompressRequestFile;
import org.usth.ict.ulake.compress.model.CompressResult;

@ApplicationScoped
public class ZipCompressor extends Compressor {

    public ZipCompressor() {
        super();
    }

    @Override
    public void compress(String bearer, List<CompressRequestFile> files, CompressResult result, CompressCallback callback) {
        Path temp = createTempZipFile();
        ZipOutputStream zipOut = null;
        FileOutputStream fos = null;
        // create zip file
        try {
            fos = new FileOutputStream(temp.toFile());
            zipOut = new ZipOutputStream(fos);
        } catch (IOException e) {
            log.error("- Cannot create zip for temporary file {}: {}", temp.toString(), e.getMessage());
            result.progress = -1L;
            callback.callback(null, false, result);
            return;
        }

        // compress files
        for (var file: files) {
            log.info("  + Zip compressing file id {}", file.fileId);
            addFileToZip(bearer, file, zipOut, callback, result);
        }

        // clean up and return
        try {
            zipOut.close();
            fos.close();
            log.info("- Zip compression finished {} files", files.size());
            result.url = temp.toFile().getAbsolutePath();
        } catch (IOException e) {
            log.error("- Cannot close temporary file {}: {}", temp.toString(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a file to the zip
     * @param result Compression result
     * @param callback Compression callback
     * @param zipOut output stream to zip
     * @param file file metadata to be compressed
     */
    private boolean addFileToZip(String bearer, CompressRequestFile file, ZipOutputStream zipOut, CompressCallback callback, CompressResult result) {
        // get file info from FileResource
        try {
            FileModel fileModel = fileService.fileInfo(file.fileId, bearer).getResp();
            log.info("   + File cid {}, name {}", fileModel.cid, fileModel.name);
            boolean success = addObjectToZip(bearer, fileModel.id, fileModel.name, zipOut);
            callback.callback(file, success, result);
        } catch (LakeServiceException e) {
            return false;
        }
        return true;
    }

    /**
     * Make a temp zip file
     * @return
     */
    private Path createTempZipFile() {
        // TODO: create temp on core Temp service
        Path temp;
        try {
            temp = Files.createTempFile("compress-", ".zip");
            return temp;
        } catch (IOException e) {
            log.error("Cannot create temporary file: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Get object from lake core and add to zip file
     * @param fileId    File id to get from lake core
     * @param fileName  Name of the file to save into zip
     * @param zipOut    output stream
     * @return
     */
    private boolean addObjectToZip(String bearer, Long fileId, String fileName, ZipOutputStream zipOut) {
        log.info("  + Preparing to fetch file id {} from core", fileId);
        InputStream fis = coreService.objectDataByFileId(fileId, bearer);
        log.info("  + Finished fetching file id {} from core", fileId);
        ZipEntry zipEntry = new ZipEntry(fileName);
        log.info("  + Start adding {}", fileName);
        try {
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
            log.info("  + Finished adding {}", fileName);
        } catch (IOException e) {
            log.error("   + Cannot Zip file {}, name {}: {}", fileId, fileName, e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
