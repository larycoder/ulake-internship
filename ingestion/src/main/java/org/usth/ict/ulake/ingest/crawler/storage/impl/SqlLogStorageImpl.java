package org.usth.ict.ulake.ingest.crawler.storage.impl;

import java.util.Map;

import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.IngestLog;
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
    public void store(IngestLog data, Map<StoreMacro, String> meta) {
        var logType = StoreMacro.valueOf(meta.get(StoreMacro.LOG_TYPE));
        if (logType == StoreMacro.PROCESS_LOG) {
            processLog.persist(data.process);
        } else if (logType == StoreMacro.FILE_LOG) {
            fileLog.persist(data.file);
        } else { /* do nothing */ }
    }

    @Override
    public IngestLog get(Map<StoreMacro, String> meta) {
        var logType = StoreMacro.valueOf(meta.get(StoreMacro.LOG_TYPE));
        var id = Long.parseLong(meta.get(StoreMacro.LOG_ID));
        var ingestLog = new IngestLog();

        if (logType == StoreMacro.PROCESS_LOG) {
            ingestLog.process = processLog.findById(id);
        } else if (logType == StoreMacro.FILE_LOG) {
            ingestLog.file = fileLog.findById(id);
        } else { /* do nothing */ }
        return ingestLog;
    }
}
