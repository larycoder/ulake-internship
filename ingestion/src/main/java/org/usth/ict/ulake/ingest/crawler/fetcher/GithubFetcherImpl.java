package org.usth.ict.ulake.ingest.crawler.fetcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.http.HttpRawRequest;
import org.usth.ict.ulake.ingest.model.http.HttpRawResponse;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.LakeHttpClient;


public class GithubFetcherImpl implements Fetcher<InputStream, String> {
    private static final Logger log = LoggerFactory.getLogger(GithubFetcherImpl.class);

    private Storage<String> store;
    private Recorder<InputStream> record;

    private Policy policy;
    private FetchConfig mode;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setup(Map<FetchConfig, String> config) {
        try {
            log.info("Parsing policy from string to object...");
            policy = mapper.readValue(
                         config.get(FetchConfig.POLICY), Policy.class);
        } catch (JsonProcessingException e) {
            log.error("Fail to parse policy from string", e);
        }
        mode = FetchConfig.valueOf(config.get(FetchConfig.MODE));
    }

    @Override
    public void setup(Storage<String> store, Recorder<InputStream> consumer) {
        this.store = store;
        this.record = consumer;
    }

    @Override
    public List<?> fetch() {
        log.info("Executing policy...");
        Interpreter engine = new Interpreter(buildRemote());
        var resultTable = engine.eval(policy);
        log.info("Executed policy");

        if (mode == FetchConfig.FETCH) {
            return resultTable.extractAsList();
        } else if (mode == FetchConfig.DOWNLOAD) {
            var status = new TableStruct<String>();

            status.addKey("link");
            status.addKey("status");

            for (var rowData : resultTable.mapRowList()) {
                var url = "";

                // get token if existed
                var token = (String) rowData.get("$token");

                List<String> resultInfo = new ArrayList<>();
                resultInfo.add(url);
                resultInfo.add((String) download(url, token));

                status.add(resultInfo);
            }
            return status.extractAsList();
        }
        return null;
    }

    private Object download(String url, String token) {
        // extract name from response
        var req = buildRemote().addPath(url);
        HttpRawResponse resp = LakeHttpClient.send(req);

        var disposition = (List<String>) resp.headers.get("content-disposition");
        String filenameIncluded = (String) disposition.get(0);
        int startPos = filenameIncluded.indexOf("filename=");
        String filename = filenameIncluded.substring(startPos + 9);

        putToRecord(resp.body, url, filename, token);

        resp.close();
        return resp.statusCode;
    }

    private void putToRecord(
        InputStream info, String url, String file, String token) {
        Map<Record, String> meta = new HashMap<>();
        meta.put(Record.LINK, url);
        meta.put(Record.NAME, file);
        meta.put(Record.TOKEN, token);
        record.record(info, meta);
    }

    private HttpRawRequest buildRemote() {
        HttpRawRequest req = new HttpRawRequest();
        req.addHeader("Accept", "application/vnd.github.v3+json");
        req.addPath("https://api.github.com");
        return req;
    }
}
