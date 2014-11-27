package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

/**
 * 
 * @author markroper
 *
 * @param <T> The subclass of 
 */
public interface IAssignmentAttributeDeserializer<T extends Assignment> {
    public void deserializeAttributes(T assignment, JsonNode node, DeserializationContext context) throws IOException;
}
