package com.scholarscore.models.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

import java.io.IOException;

/**
 * User: jordan
 * Date: 11/30/14
 * Time: 8:16 PM
 */
public class AssignmentDeserializerFactory extends JsonDeserializer<Assignment> {

    @SuppressWarnings("rawtypes")
    @Override
    public Assignment deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);
        IAssignmentDeserializer deserializer = null;
        if(null != node.get(JsonKeyConstants.TYPE)
                && !node.get(JsonKeyConstants.TYPE).isNull()) {
            switch(node.get(JsonKeyConstants.TYPE).asText()) {
                case "GRADED":
                    deserializer = new GradedAssignmentDeserializer();
                    break;
                case "ATTENDANCE":
                    deserializer = new AttendanceAssignmentDeserializer();
                    break;
                default:
                    throw new ObjectParsingException("warehouse.api.error.assignment.unsupportedtype", 
                            new Object[]{ node.get(JsonKeyConstants.TYPE).asText() });
            }

        } else {
            throw new ObjectParsingException("warehouse.api.error.assignment.unsupportedtype", 
                    new Object[]{ "null" });
        }
        return deserializer.deserializeAttributes(node);
    }


}
