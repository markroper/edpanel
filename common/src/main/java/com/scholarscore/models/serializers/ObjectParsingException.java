package com.scholarscore.models.serializers;

/**
 * The object parsing exception is thrown when the custom deserialization code
 * is unable to parse valid input to generate the appropriate subclasses. The 
 * exception does not indicate that the input JSON is invalid, rather that it
 * cannot be marshaled into a system-accepted model. 
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class ObjectParsingException extends RuntimeException {
    private Object[] args;
    
    public ObjectParsingException(String msg, Object[] args) {
        super(msg);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
