package com.scholarscore.models.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.AttendanceAssignment;

public class AttendanceAssignmentDeserializer extends AssignmentDeserializer<AttendanceAssignment> {

    @Override
    public Assignment deserializeAttributes(JsonNode node) throws IOException {
        AttendanceAssignment assignment = getAssignment();
        if(null != node.get(JsonKeyConstants.DATE)) {
            assignment.setDate(new Date(node.get(JsonKeyConstants.DATE).asLong()));
        }
        return super.deserializeAttributes(node);
    }

    @Override
    public AttendanceAssignment newInstance() {
        return new AttendanceAssignment();
    }
}
