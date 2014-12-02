package com.scholarscore.models.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.GradedAssignment;

public class GradedAssignmentDeserializer extends AssignmentDeserializer<GradedAssignment> {

    @Override
    public Assignment deserializeAttributes(JsonNode node) throws IOException {
        GradedAssignment assignment = getAssignment();
        if(null != node.get(JsonKeyConstants.ASSIGNED_DATE)) {
            assignment.setAssignedDate(new Date(node.get(JsonKeyConstants.ASSIGNED_DATE).asLong()));
        }
        if(null != node.get(JsonKeyConstants.DUE_DATE)) {
            assignment.setDueDate(new Date(node.get(JsonKeyConstants.DUE_DATE).asLong()));
        }
        return super.deserializeAttributes(node);
    }

    @Override
    public GradedAssignment newInstance() {
        return new GradedAssignment();
    }
}
