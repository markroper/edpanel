package com.scholarscore.api.util;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knappsack.swagger4springweb.util.ScalaObjectMapper;

/**
 * Created by markroper on 11/23/15.
 */
public class EdPanelScalaObjectMapper extends ScalaObjectMapper {

    public EdPanelScalaObjectMapper() {
        super();
        registerModule(new JavaTimeModule());
//        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
