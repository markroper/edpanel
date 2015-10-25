package com.scholarscore.models.query.expressions.operators;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

/**
 * Marker interface indicating that the implementer is a valid operator in the
 * warehouse reporting filter criteria model.
 * 
 * @author markroper
 *
 */
@JsonDeserialize(using = IOperator.OperatorDeserializer.class)
public interface IOperator {
    
    public String name();
    
    public class OperatorDeserializer extends JsonDeserializer<IOperator>{
        @Override
        public IOperator deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);
            IOperator expOp = null;
            try {
                expOp = ComparisonOperator.valueOf(node.asText());
            } catch (Exception e) {
                
            }
            if(null == expOp) {
                expOp = BinaryOperator.valueOf(node.asText());
            }
            return expOp;
        }
        
    }

}
