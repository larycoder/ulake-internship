package org.usth.ict.ulake.ingest.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PolicyPattern {
    public String value;

    @JsonInclude(Include.NON_NULL)
    public Map<String, List<String>> var;

}
