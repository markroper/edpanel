package com.scholarscore.api.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knappsack.swagger4springweb.util.ScalaObjectMapper;

/**
 * In order to use Swagger for API documentation, we need to use the ScalaObjectMapper for
 * Jackson serialization and deserialization.  We extend it here so that we can register our configuration
 * to support Java8 Date APIs.
 *
 * Created by markroper on 11/23/15.
 */
public class EdPanelScalaObjectMapper extends ScalaObjectMapper {

    public EdPanelScalaObjectMapper() {
        super();
        registerModule(new JavaTimeModule());
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
