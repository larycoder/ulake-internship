package org.usth.ict.ulake.ingest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PolicyData {
    @JsonInclude(Include.NON_NULL)
    public PolicyRequest req;

    @JsonInclude(Include.NON_NULL)
    public PolicyPattern pattern;

    public String map;
}
