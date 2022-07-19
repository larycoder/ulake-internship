package org.usth.ict.ulake.ingest.crawler.storage.impl;

import java.util.HashMap;
import java.util.Map;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.FileLog;
import org.usth.ict.ulake.ingest.model.IngestLog;
import org.usth.ict.ulake.ingest.model.ProcessLog;
import org.usth.ict.ulake.ingest.model.macro.StoreMacro;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.ProcessLogRepo;

public class SqlLogStorageImpl implements Storage<IngestLog> {
    private ProcessLogRepo processLog;
    private FileLogRepo fileLog;

    public SqlLogStorageImpl(ProcessLogRepo processLog, FileLogRepo fileLog) {
        this.processLog = processLog;
        this.fileLog = fileLog;
    }

    @Override
    public void setup(Map<StoreMacro, String> config) {
        // TODO Empty
    }

    @Override
    public Map<String, String> store(
        IngestLog data, Map<StoreMacro, String> meta) {
        var result = new HashMap<String, String>();
        var logType = StoreMacro.valueOf(meta.get(StoreMacro.LOG_TYPE));
        var storeOpt = StoreMacro.valueOf(meta.get(StoreMacro.STORE_OPT));

        if (logType == StoreMacro.PROCESS_LOG && storeOpt == StoreMacro.CREATE) {
            processLog.persist(data.process);
            result.put(StoreMacro.LOG_ID.toString(), data.process.id.toString());
        } else if (logType == StoreMacro.PROCESS_LOG && storeOpt == StoreMacro.UPDATE) {
            updateProcess(data.process);
            result.put(StoreMacro.LOG_ID.toString(), data.process.id.toString());
        } else if (logType == StoreMacro.FILE_LOG && storeOpt == StoreMacro.CREATE) {
            fileLog.persist(data.file);
            result.put(StoreMacro.LOG_ID.toString(), data.file.id.toString());
        } else if (logType == StoreMacro.FILE_LOG && storeOpt == StoreMacro.UPDATE) {
            updateFile(data.file);
            result.put(StoreMacro.LOG_ID.toString(), data.file.id.toString());
        }

        return result;
    }

    @Override
    public IngestLog get(Map<StoreMacro, String> meta) {
        var logType = StoreMacro.valueOf(meta.get(StoreMacro.LOG_TYPE));
        var id = Long.parseLong(meta.get(StoreMacro.LOG_ID));
        var ingestLog = new IngestLog();

        if (logType == StoreMacro.PROCESS_LOG)
            ingestLog.process = processLog.findById(id);
        else if (logType == StoreMacro.FILE_LOG)
            ingestLog.file = fileLog.findById(id);

        return ingestLog;
    }

    private void updateProcess(ProcessLog entry) {
        ProcessLog log = processLog.findById(entry.id);

        if (entry.ownerId != null) log.ownerId = entry.ownerId;
        if (entry.folderId != null) log.folderId = entry.folderId;
        if (entry.query != null) log.query = entry.query;
        if (entry.endTime != null) log.endTime = entry.endTime;
        if (entry.creationTime != null)
            log.creationTime = entry.creationTime;
        if (!Utils.isEmpty(entry.description))
            log.description = entry.description;

        processLog.persist(log);
    }

    private void updateFile(FileLog entry) {
        FileLog log = fileLog.findById(entry.id);

        if (entry.fileId != null) log.fileId = entry.fileId;
        if (entry.status != null) log.status = entry.status;
        if (entry.uploadTime != null)
            log.uploadTime = entry.uploadTime;
        if (entry.process != null && entry.process.id != null)
            log.process = processLog.findById(entry.process.id);

        fileLog.persist(log);
    }
}
