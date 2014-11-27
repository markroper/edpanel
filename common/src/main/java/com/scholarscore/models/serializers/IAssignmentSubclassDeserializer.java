package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

/**
 * Defines the behavior of a JSON deserializer for a subclass of Assignment, for example
 * GradedAssignment or AttendanceAssignment.
 * 
 * @author markroper
 * @param <T> The subclass of Assignment that deserializer is capable of marshalling
 */
public interface IAssignmentSubclassDeserializer<T extends Assignment> {

    public T deserializeAssignmentSubclass(JsonNode node, DeserializationContext context) 
        throws IOException;
    
}
