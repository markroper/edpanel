package com.scholarscore.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * The class encapsulates an instance of an object of type T, an ErrorCode instance, and an 
 * Object array of parameters for the error message which are used to support localization of
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
    protected StatusCode code;
    protected T value;
    
    public ServiceResponse(T val) {
        value = val;
    }
    
    public ServiceResponse(StatusCode code) {
        this.code = code;
    }

    public StatusCode getCode() {
        return code;
    }

    public void setCode(StatusCode code) {
        this.code = code;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
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
                Objects.equals(this.code, other.code);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value, code);
    }
}
