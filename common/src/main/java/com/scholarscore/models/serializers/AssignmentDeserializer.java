package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

public abstract class AssignmentDeserializer
        implements IAssignmentAttributeDeserializer<Assignment> {

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
    }
}
