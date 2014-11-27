package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

public class AssignmentDeserializer extends JsonDeserializer<Assignment> 
        implements IAssignmentAttributeDeserializer<Assignment> {

    @SuppressWarnings("rawtypes")
    @Override
    public Assignment deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);
        IAssignmentSubclassDeserializer deserializer = null;
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
                    throw new IOException("Type not supported");
            }
            
        } else {
            throw new IOException("Cannot resolve the assignment type");
        }
        return deserializer.deserializeAssignmentSubclass(node, ctxt);
    }

    @Override
    public void deserializeAttributes(Assignment assignment, JsonNode node,
            DeserializationContext context) throws IOException {
        if(null != node.get(JsonKeyConstants.NAME)){
            assignment.setName(node.get(JsonKeyConstants.NAME).asText());
        }
        
        if(null != node.get(JsonKeyConstants.ID)){
            assignment.setId(node.get(JsonKeyConstants.ID).asLong());
        }
        
        if(null != node.get(JsonKeyConstants.TYPE)){
            assignment.setType(node.get(JsonKeyConstants.TYPE).asText());
        }
        
        if(null != node.get(JsonKeyConstants.COURSE_ID)){
            assignment.setCourseId(node.get(JsonKeyConstants.COURSE_ID).asLong());
        } 
    }
}
