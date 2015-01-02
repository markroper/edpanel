package com.scholarscore.api.util;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The class encapsulated an instance of an object, an ErrorCode instance, and an 
 * Object array of parameters for the error message, which are used to support localization of
 * error messages.  The intent is that a ServiceResponse instance can be used as a return type
 * for a method that needs to return an object of a particular type, or in degenerate cases,
 * to return a descriptive error code.
 * 
 * @author markroper
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> implements Serializable {
    protected ErrorCode error;
    protected T value;
    protected Object[] errorParams;
    
    public ServiceResponse(T val) {
        value = val;
    }
    
    public ServiceResponse(ErrorCode code) {
        error = code;
    }
    
    public ServiceResponse(ErrorCode code, Object[] params) {
        this(code);
        errorParams = params;
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
    
    public Object[] getErrorParams() {
        return errorParams;
    }

    public void setErrorParams(Object[] errorParams) {
        this.errorParams = errorParams;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ServiceResponse<?> other = (ServiceResponse<?>) obj;
        return Objects.equals(this.value, other.value) && 
                Objects.equals(this.error, other.error) &&
                Objects.equals(this.errorParams, other.errorParams);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value, error, errorParams);
    }
}
