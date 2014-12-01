package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

/**
 * Defines the behavior of a JSON deserializer for a subclass of Assignment, for example
 * GradedAssignment or AttendanceAssignment.
 * 
 * @author markroper
 */
public interface IAssignmentDeserializer {
    public Assignment deserializeAttributes(JsonNode node) throws IOException;
}
