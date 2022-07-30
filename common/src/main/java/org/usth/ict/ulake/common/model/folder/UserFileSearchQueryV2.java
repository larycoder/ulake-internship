package org.usth.ict.ulake.common.model.folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.query.HqlResult;
import org.usth.ict.ulake.common.query.KeywordOpt;
import org.usth.ict.ulake.common.query.NumberOpt;
import org.usth.ict.ulake.common.query.Queryable;
import org.usth.ict.ulake.common.query.StringOpt;

public class UserFileSearchQueryV2 implements Queryable {
    @Schema(description = "Filter by file id")
    public NumberOpt<Long> id;

    @Schema(description = "Filter by file ownerId")
    public NumberOpt<Long> ownerIds;

    @Schema(description = "A keyword that responded filenames must contain.")
    public KeywordOpt keywords;

    @Schema(description = "Size of file.")
    public NumberOpt<Long> size;

    @Schema(description = "MIME keyword that the file MIME must contain.")
    public StringOpt mime;

    public UserFileSearchQueryV2 () {}

    @Override
    public HqlResult getHQL(String property) {
        List<String> hql = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        HqlResult hr;

        if (id != null) {
            hr = id.getHQL("id");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (ownerIds != null) {
            hr = ownerIds.getHQL("ownerId");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (keywords != null) {
            hr = keywords.getHQL("keyword");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (size != null) {
            hr = size.getHQL("size");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        if (mime != null) {
            hr = mime.getHQL("mime");
            hql.add("(" + hr.hql + ")");
            params.putAll(hr.params);
        }

        return new HqlResult(String.join(" AND ", hql), params);
    }
}
