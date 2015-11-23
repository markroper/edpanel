package com.scholarscore.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The ObjectMapper to reuse throughout the project so that we don't have to
 * initialize many of them at runtime cost and with the potential for misconfiguration.
 *
 * @author markroper on 11/23/15.
 */
public class EdPanelObjectMapper {
    public static final ObjectMapper MAPPER = new ObjectMapper().
        setSerializationInclusion(JsonInclude.Include.NON_NULL).
        registerModule(new JavaTimeModule()).
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
}
