package org.usth.ict.ulake.ingest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PolicyRequest {
    public String method;
    public PolicyPattern path;

    @JsonInclude(Include.NON_NULL)
    public PolicyPattern head;

    @JsonInclude(Include.NON_NULL)
    public PolicyPattern body;
}
