package com.scholarscore.models.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.AttendanceAssignment;

public class AttendanceAssignmentDeserializer extends AssignmentDeserializer 
implements IAssignmentSubclassDeserializer<AttendanceAssignment>{

    @Override
    public AttendanceAssignment deserializeAssignmentSubclass(JsonNode node,
            DeserializationContext context) throws IOException {
        AttendanceAssignment assignment = new AttendanceAssignment();
        if(null != node.get(JsonKeyConstants.DATE)) {
            assignment.setDate(new Date(node.get(JsonKeyConstants.DATE).asLong()));
        }
        deserializeAttributes(assignment, node, context);
        return assignment;
    }

}
