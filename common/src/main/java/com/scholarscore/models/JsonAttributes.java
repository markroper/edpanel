package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class would not need to exist if JsonNode could be serialized to and from a JSON string 
 * by Jackson as an instance member on another class.  But alas, it can't.  This class helps
 * Jackson figure out how to go from a JSON string to an instance of a JsonNode (its insane it can't do that)
 * by using the @JsonCreator annotation on the String constructor.
 * 
 * This class can be used on any other POJO that needs to have an arbitrary and un-validated
 * JSON document stored on it.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonAttributes implements Serializable  {
    private static final ObjectMapper mapper = new ObjectMapper();
    protected transient JsonNode jsonNode;
    @JsonIgnore
    protected String jsonString;
    
    public JsonAttributes() {
        
    }
    public JsonAttributes(JsonNode node) {
        this.jsonNode = node;
        try {
            this.jsonString = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            //NO OP
        }
    }
    
    @JsonCreator
    public JsonAttributes(String jsonString) throws IOException {
        this.jsonString = jsonString;
        this.jsonNode = convertStringToJson(jsonString);   
    }
    
    public JsonNode convertStringToJson(String string) throws JsonProcessingException, IOException {
        JsonNode answer = null;
        if(null == string) {
            answer = JsonNodeFactory.instance.nullNode();
        } else {
            answer = mapper.readTree(string);
        }
        return answer;
    }
    
    public JsonNode getJsonNode() {
        return jsonNode;
    }
    
    public void setJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        try {
            this.jsonString = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            //No op?
        }
    }
    
    @Converter
    public static class JsonAttributesConverter implements AttributeConverter<JsonAttributes, String> {   
        public String convertToDatabaseColumn(JsonAttributes attribute) {
            String returnValue = null;
            if(null != attribute) {
                returnValue = attribute.jsonString;
            }
            return returnValue;
        }

        @Override
        public JsonAttributes convertToEntityAttribute(String dbData) {
            JsonAttributes attrs = null;
            try {
                attrs = new JsonAttributes(dbData);
            } catch (IOException e) {
                //NO OP
            }
            return attrs;
        }

    }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }
    final JsonAttributes other = (JsonAttributes) obj;
    return Objects.equals(this.jsonString, other.jsonString)
            && Objects.equals(this.jsonNode, other.jsonNode);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(jsonString, jsonNode);
    }
}
