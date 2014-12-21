package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

public abstract class AssignmentDeserializer<T extends Assignment> extends BaseDeserializer<T>
        implements IAssignmentDeserializer {

    // this isn't strictly necessary, but nice so that subclasses don't need to call 'getObject'
    protected T getAssignment() {
        return getObject();
    }

    @Override
    public Assignment deserializeAttributes(JsonNode node) throws IOException {
        Assignment assignment = getAssignment();
        if(null != node.get(JsonKeyConstants.NAME)){
            assignment.setName(node.get(JsonKeyConstants.NAME).asText());
        }
        
        if(null != node.get(JsonKeyConstants.ID)){
            assignment.setId(node.get(JsonKeyConstants.ID).asLong());
        }
        
        if(null != node.get(JsonKeyConstants.TYPE)){
            assignment.setType(node.get(JsonKeyConstants.TYPE).asText());
        }
        return assignment;
    }
}
