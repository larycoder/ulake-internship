package org.usth.ict.ingest.crawler.fetcher;

import org.usth.ict.ingest.crawler.fetcher.cpl.Interpreter;
import org.usth.ict.ingest.crawler.fetcher.cpl.struct.TableStruct;
import org.usth.ict.ingest.crawler.recorder.Recorder;
import org.usth.ict.ingest.crawler.storage.Storage;
import org.usth.ict.ingest.models.macro.FetchConfig;
import org.usth.ict.ingest.models.macro.Record;
import org.usth.ict.ingest.utils.RestClientUtil;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GithubFetcherImpl implements Fetcher {
    private Storage store;
    private Recorder record;

    private Map policy;
    private int mode;

    @Override
    public void setup(HashMap config) {
        policy = (Map) config.get(FetchConfig.POLICY);
        mode = (int) config.get(FetchConfig.MODE);
    }

    @Override
    public void setup(Storage store, Recorder consumer) {
        this.store = store;
        this.record = consumer;
    }

    @Override
    public List fetch() {
        Interpreter engine = new Interpreter(buildRemote());
        TableStruct resultTable = engine.eval(policy);

        if (mode == FetchConfig.FETCH) {
            return resultTable.extractAsList();
        } else if (mode == FetchConfig.DOWNLOAD) {
            TableStruct status = new TableStruct();

            status.addKey("link");
            status.addKey("status");

            for (Map data : resultTable.rowList()) {
                var rowData = (List) data.get("list");
                var url = (String) rowData.get(rowData.size() - 1);

                // get token if existed
                var rowMap = (Map) data.get("json");
                var token = (String) rowMap.get("$token");

                List resultInfo = new ArrayList();
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

        Map head = resp.getHeaders();
        List disposition = (List) head.get("content-disposition");
        String filenameIncluded = (String) disposition.get(0);
        int startPos = filenameIncluded.indexOf("filename=");
        String filename = filenameIncluded.substring(startPos+9);

        putToRecord(info, url, filename, token);

        resp.close();
        return status;
    }

    private void putToRecord(
            InputStream info, String url, String file, String token) {
        HashMap meta = new HashMap();
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
