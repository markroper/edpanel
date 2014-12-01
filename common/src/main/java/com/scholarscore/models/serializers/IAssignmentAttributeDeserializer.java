package com.scholarscore.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.scholarscore.models.Assignment;

/**
 * Defines the behavior that all Assignment subclass Jackson deserializers should implement.  Given
 * an instance of T or a subclass of T, and a JsonNode and context, the method should populate attributes
 * from the JsonNode onto the Assignment instance.
 * 
 * @author markroper
 *
 * @param <T> The subclass of 
 */
public interface IAssignmentAttributeDeserializer<T extends Assignment> {
    /**
     * Given an assignment, node and context, populate attributes on the assignment instance from the node 
     * making use of the context
     * 
     * @param assignment The assignment to populate attributes on
     * @param node The JsonNode containing assignment attribuets
     * @param context The deserialization context
     * @throws IOException
     */
    public void deserializeAttributes(T assignment, JsonNode node, DeserializationContext context) throws IOException;
}
