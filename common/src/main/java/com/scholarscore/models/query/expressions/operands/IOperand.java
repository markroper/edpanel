package com.scholarscore.models.query.expressions.operands;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.expressions.operators.IOperator;
import com.scholarscore.models.serializers.JsonKeyConstants;

/**
 * Marker interface indicating that the implementing entity is a valid operand
 * in the warehouse reporting filter expression model.
 * 
 * @author markroper
 *
 */
@JsonDeserialize(using = IOperand.OperandDeserializer.class)
public interface IOperand {
    /**
     * Returns the type of the operand
     * @return
     */
    public OperandType getType();
    
    public class OperandDeserializer extends JsonDeserializer<IOperand>{
        private static final String VALUE = "value";
        private static final String LHS = "leftHandSide";
        private static final String OPERATOR = "operator";
        private static final String RHS = "rightHandSide";
        
        @Override
        public IOperand deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);
            IOperand operand = null;
            if(null != node.get(JsonKeyConstants.TYPE)) {
                String operandType = node.get(JsonKeyConstants.TYPE).asText();
                OperandType op = OperandType.valueOf(operandType);
                ObjectMapper mapper = new ObjectMapper();
                //mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
                SimpleModule simpleModule = new SimpleModule("SimpleModule");
                simpleModule.addDeserializer(IOperand.class, new IOperand.OperandDeserializer());
                mapper.registerModule(simpleModule);
                switch(op) {
                    case DATE:
                        DateOperand dateOp = new DateOperand();
                        dateOp.setValue(new Date(node.get(VALUE).asLong()));
                        operand = dateOp;
                        break;
                    case STRING:
                        StringOperand strOp = new StringOperand();
                        strOp.setValue(node.get(VALUE).asText());
                        operand = strOp;
                        break;
                    case NUMERIC:
                        NumericOperand numOp = new NumericOperand();
                        numOp.setValue(node.get(VALUE).asDouble());
                        operand = numOp;
                        break;
                    case DIMENSION:
                        DimensionOperand dimOp = new DimensionOperand();
                        dimOp.setValue(Dimension.valueOf(node.get(VALUE).asText()));
                        operand = dimOp;
                        break;
                    case EXPRESSION:    
                        IOperand lhs = mapper.treeToValue(node.get(LHS), IOperand.class);
                        IOperand rhs = mapper.treeToValue(node.get(RHS), IOperand.class);
                        IOperator expOp = null;
                        try {
                            expOp = ComparisonOperator.valueOf(node.get(OPERATOR).asText());
                        } catch (Exception e) {
                            
                        }
                        if(null == expOp) {
                            expOp = BinaryOperator.valueOf(node.get(OPERATOR).asText());
                        }
                        operand = new Expression(lhs, expOp, rhs);
                        break;
                    default:
                        throw new RuntimeException("Unsupported operand");
                }
            }
            //jp.close();
            return operand;
        }

    }
}
