package com.scholarscore.util;

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.UiAttributes;

@Converter
public class ObjectNodeConverter implements AttributeConverter<UiAttributes, String> {
    protected ObjectMapper mapper = new ObjectMapper();
    
    public String convertToDatabaseColumn(UiAttributes attribute) {
        String stringValue = null;
        if(null != attribute) {
            try {
                stringValue = mapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                //NO OP
            }
        }
        return stringValue;
    }

    @Override
    public UiAttributes convertToEntityAttribute(String dbData) {
        UiAttributes attrs = null;
        try {
            attrs = mapper.readValue(dbData, UiAttributes.class);
        } catch (IOException e) {
            //NO OP
        }
        return attrs;
    }

}
