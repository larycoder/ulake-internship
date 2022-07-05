package org.usth.ict.ulake.extract.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractResultFile;
import org.usth.ict.ulake.extract.model.ExtractResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ZipCompressor extends Compressor {

    public ZipCompressor(String token, CoreService coreService, FileService fileService) {
        super(token, coreService, fileService);
    }

    @Override
    public void compress(List<ExtractResultFile> files, ExtractResult result, CompressCallback callback) {
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
            addFileToZip(file, zipOut, callback, result);
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
    private boolean addFileToZip(ExtractResultFile file, ZipOutputStream zipOut, CompressCallback callback, ExtractResult result) {
        // get file info from FileResource
        var fileInfo = fileService.fileInfo(file.fileId, token);
        if (fileInfo.getCode() != 200) {
            log.error("   + Cannot find file with id {}", file.fileId);
            return false;
        }
        if (fileInfo.getResp() instanceof Map) {
            // get file model from response
            final var map = (Map<String, Object>) fileInfo.getResp();
            final ObjectMapper mapper = new ObjectMapper();
            FileModel fileModel = mapper.convertValue(map, FileModel.class);

            log.info("   + File cid {}, name {}", fileModel.cid, fileModel.name);
            boolean success = addObjectToZip(fileModel.id, fileModel.name, zipOut);
            callback.callback(file, success, result);
            return true;
        }
        return false;
    }

    /**
     * Make a temp zip file
     * @return
     */
    private Path createTempZipFile() {
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
    private boolean addObjectToZip(Long fileId, String fileName, ZipOutputStream zipOut) {
        log.info("  + Preparing to fetch file id {} from core", fileId);
        InputStream fis = coreService.objectDataByFileId(fileId, token);
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