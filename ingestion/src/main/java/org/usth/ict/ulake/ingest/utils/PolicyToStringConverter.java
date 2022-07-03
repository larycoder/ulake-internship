package org.usth.ict.ulake.ingest.utils;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.ingest.model.Policy;

public class PolicyToStringConverter
    implements AttributeConverter<Policy, String> {

    @Override
    public String convertToDatabaseColumn(Policy attribute) {
        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Policy convertToEntityAttribute(String dbData) {
        try {
            return new ObjectMapper().readValue(dbData, Policy.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
