package com.scholarscore.models.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.GradedAssignment;

public class GradedAssignmentDeserializer extends AssignmentDeserializer 
        implements IAssignmentSubclassDeserializer<GradedAssignment> {

    @Override
    public GradedAssignment deserializeAssignmentSubclass(JsonNode node,
            DeserializationContext context) throws IOException {
        GradedAssignment assignment = new GradedAssignment();
        if(null != node.get(JsonKeyConstants.ASSIGNED_DATE)) {
            assignment.setAssignedDate(new Date(node.get(JsonKeyConstants.ASSIGNED_DATE).asLong()));
        }
        if(null != node.get(JsonKeyConstants.DUE_DATE)) {
            assignment.setDueDate(new Date(node.get(JsonKeyConstants.DUE_DATE).asLong()));
        }
        deserializeAttributes(assignment, node, context);
        return assignment;
    }

}
