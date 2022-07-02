package org.usth.ict.ulake.ingest.crawler.fetcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ulake.ingest.crawler.recorder.Recorder;
import org.usth.ict.ulake.ingest.crawler.storage.Storage;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.model.macro.Record;
import org.usth.ict.ulake.ingest.utils.RestClientUtil;


public class GithubFetcherImpl implements Fetcher {
    private Storage store;
    private Recorder record;

    private Map<String, Object> policy;
    private FetchConfig mode;

    @Override
    public void setup(HashMap<?, ?> config) {
        policy = (Map<String, Object>) config.get(FetchConfig.POLICY);
        mode = (FetchConfig) config.get(FetchConfig.MODE);
    }

    @Override
    public void setup(Storage store, Recorder consumer) {
        this.store = store;
        this.record = consumer;
    }

    @Override
    public List<?> fetch() {
        Interpreter engine = new Interpreter(buildRemote());
        TableStruct resultTable = engine.eval(policy);

        if (mode == FetchConfig.FETCH) {
            return resultTable.extractAsList();
        } else if (mode == FetchConfig.DOWNLOAD) {
            TableStruct status = new TableStruct();

            status.addKey("link");
            status.addKey("status");

            for (var data : resultTable.rowList()) {
                var rowData = (List<?>) data.get("list");
                var url = (String) rowData.get(rowData.size() - 1);

                // get token if existed
                var rowMap = (Map<?, ?>) data.get("json");
                var token = (String) rowMap.get("$token");

                List<Object> resultInfo = new ArrayList<>();
                resultInfo.add(url);
                resultInfo.add(download(url, token));

                status.add(resultInfo);
            }
            return status.extractAsList();
        }
        return null;
    }

    private Object download(String url, String token) {
        // extract name from response
        Response resp = buildRemote()
                .setPath(url)
                .buildRequest()
                .get();

        var info = (InputStream) resp.getEntity();
        int status = resp.getStatus();

        Map<?, ?> head = resp.getHeaders();
        var disposition = (List<?>) head.get("content-disposition");
        String filenameIncluded = (String) disposition.get(0);
        int startPos = filenameIncluded.indexOf("filename=");
        String filename = filenameIncluded.substring(startPos+9);

        putToRecord(info, url, filename, token);

        resp.close();
        return status;
    }

    private void putToRecord(
            InputStream info, String url, String file, String token) {
        HashMap<Object, Object> meta = new HashMap<>();
        meta.put(Record.LINK, url);
        meta.put(Record.NAME, file);
        meta.put(Record.TOKEN, token);
        record.record(info, meta);
    }

    private RestClientUtil buildRemote() {
        RestClientUtil remote = new RestClientUtil()
                .setHead("Accept", "application/vnd.github.v3+json")
                .setBaseURL("https://api.github.com")
                .setHttpRedirect(true);
        return remote;
    }
}
