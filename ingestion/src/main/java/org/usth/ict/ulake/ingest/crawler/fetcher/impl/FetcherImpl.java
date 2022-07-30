package org.usth.ict.ulake.ingest.crawler.fetcher.impl;

import java.io.InputStream;
import java.net.http.HttpClient.Redirect;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.FileLog;
import org.usth.ict.ulake.ingest.model.IngestLog;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.http.HttpConfigure;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.model.macro.StoreMacro;
import org.usth.ict.ulake.ingest.utils.LakeHttpClient;

public class FetcherImpl implements Fetcher<IngestLog, InputStream> {
    private static Logger sysLog = LoggerFactory.getLogger(FetcherImpl.class);

    private HttpRawRequest baseReq;
    private FetchConfig mode;

    private Map<StoreMacro, String> reqProcConf = new HashMap<>();
    {
        reqProcConf.put(StoreMacro.LOG_TYPE, StoreMacro.PROCESS_LOG.toString());
    }

    private Map<StoreMacro, String> storeFileConf = new HashMap<>();
    {
        storeFileConf.put(StoreMacro.LOG_TYPE, StoreMacro.FILE_LOG.toString());
        storeFileConf.put(StoreMacro.STORE_OPT, StoreMacro.CREATE.toString());
    }

    private ObjectMapper mapper = new ObjectMapper();

    private Recorder<InputStream> consumer;
    private Storage<IngestLog> db;

    /**
     * Initialize default fetcher.
     * Aware Behavior: base request client always follows redirection
     * Aware Behavior: base request client always delay 200 milliseconds
     * */
    public FetcherImpl() {
        baseReq = new HttpRawRequest();
        baseReq.conf = new HttpConfigure();
        baseReq.conf.redirectStrategy = Redirect.ALWAYS;
    }

    @Override
    public void setup(Map<FetchConfig, String> config) {
        mode = FetchConfig.valueOf(config.get(FetchConfig.MODE));
        reqProcConf.put(StoreMacro.LOG_ID, config.get(FetchConfig.PROCESS_ID));
    }

    @Override
    public void setup(
        Storage<IngestLog> store, Recorder<InputStream> consumer) {
        this.consumer = consumer;
        this.db = store;
    }

    @Override
    public Map<String, Object> fetch(Policy policy) {
        Interpreter engine = new Interpreter(baseReq);
        var resultTable = engine.eval(policy);
        var respTable = new TableStruct<String>(resultTable.getKey());
        respTable.addKey(FetchConfig.STATUS.toString());

        if (mode == FetchConfig.FETCH) {
            return resultTable.extractAsMap();
        } else if (mode == FetchConfig.DOWNLOAD) {
            while (resultTable.rowSize() > 0) {
                Map<String, String> param = resultTable.stackPopJson();
                for (var request : engine.visitReturn(engine.ret, param)) {
                    Boolean status = logFile(saveAsFile(request, param), param);
                    param.put(FetchConfig.STATUS.toString(), status.toString());
                    respTable.add(param);
                }
            }
            return respTable.extractAsMap();
        } else {
            return null;
        }
    }

    /**
     * Save body of response to file.
     * Aware Behavior: filename is discovered from header
     * Aware Behavior: if header fail, filename is discovered from URI
     * Aware Behavior: filename in header must follow content-disposition syntax
     * Aware Behavior: file mime is retrieved from content-type
     * */
    private Map<String, String> saveAsFile(
        HttpRawRequest request, Map<String, String> meta) {
        String filename;
        HttpRawResponse resp;

        try {
            resp = LakeHttpClient.send(request);
        } catch (Exception e) {
            sysLog.error("Could not run request for meta {}.", meta);
            e.printStackTrace();
            return null;
        }

        var cd = resp.headers.get("content-disposition");
        if (cd != null && cd.get(0).contains("filename")) { // filename in header
            // NOTE: disposition string expected example:
            // attachment; filename=Hello_world.txt
            filename = cd.get(0).split("=")[1].strip();
        } else { // filename from URI
            String[] uri = resp.uri.split("\\?")[0].strip().split("/");
            filename = uri[uri.length - 1].strip();
        }

        String contentType = null;
        var contentTypeList = resp.headers.get("content-type");
        if (!Utils.isEmpty(contentTypeList))
            contentType = contentTypeList.get(0);

        Map<Record, String> myMeta = new HashMap<>();
        myMeta.put(Record.FILE_NAME, filename);
        myMeta.put(Record.FILE_MIME, contentType);

        sysLog.info("Start upload file {} to lake...", filename);
        consumer.record(resp.body, myMeta);
        sysLog.info("Done process for file {}.", filename);

        return consumer.info();
    }

    /**
     * Record crawled file status to database.
     * */
    private Boolean logFile(
        Map<String, String> fileInfo, Map<String, String> meta) {

        var log = db.get(reqProcConf);
        log.file = new FileLog();
        log.file.process = log.process;
        log.file.uploadTime = new Date().getTime();

        if (fileInfo == null) {
            log.file.status = false;
        } else {
            String status = fileInfo.get(Record.STATUS.toString());
            String fileId = fileInfo.get(Record.OBJECT_ID.toString());
            String size = fileInfo.get(Record.FILE_SIZE.toString());

            log.file.status = Boolean.parseBoolean(status);
            if (!Utils.isEmpty(size))
                log.file.size = Long.parseLong(size);
            if (!Utils.isEmpty(fileId))
                log.file.fileId = Long.parseLong(fileId);
        }

        try {
            log.file.setMeta(mapper.writeValueAsString(meta));
        } catch (JsonProcessingException e) {
            log.file.meta = null;
        }

        db.store(log, storeFileConf);
        return log.file.status;
    }
}
