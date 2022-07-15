package org.usth.ict.ulake.ingest.crawler.fetcher.impl;

import java.io.InputStream;
import java.net.http.HttpClient.Redirect;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.ingest.crawler.fetcher.Fetcher;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.http.HttpConfigure;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.LakeHttpClient;

public class FetcherImpl implements Fetcher<String, InputStream> {
    private static final Logger log = LoggerFactory.getLogger(FetcherImpl.class);
    private FetchConfig mode;
    private Recorder<InputStream, String> consumer;
    private HttpRawRequest baseReq;

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
    }

    @Override
    public void setup(
        Storage<String> store, Recorder<InputStream, String> consumer) {
        this.consumer = consumer;
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
                // TODO: param does not show list of request in resp status
                for (var request : engine.visitReturn(engine.ret, param)) {
                    param.put(FetchConfig.STATUS.toString(),
                              save(request, param).toString());
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
     * */
    private Boolean save(HttpRawRequest request, Map<String, String> meta) {
        String filename;
        HttpRawResponse resp = LakeHttpClient.send(request);

        String cd = resp.headers.get("content-disposition").get(0);
        if (cd != null && cd.contains("filename")) { // filename in header
            // NOTE: disposition string expected example:
            // attachment; filename=Hello_world.txt
            filename = cd.split("=")[1].strip();
        } else { // filename from URI
            String[] uri = resp.uri.split("/");
            filename = uri[uri.length - 1].strip();
        }

        Map<Record, String> myMeta = new HashMap<>();
        myMeta.put(Record.FILE_NAME, filename);
        consumer.record(resp.body, myMeta);
        return Boolean.valueOf(
                   consumer.info().get(Record.STATUS.toString()));
    }
}
