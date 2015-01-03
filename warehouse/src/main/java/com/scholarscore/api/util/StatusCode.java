package com.scholarscore.api.util;

import java.util.Arrays;

import javax.xml.bind.annotation.*;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The ErrorCode object returned by the API in those cases when the
 * the expected object is not available due to a 404, 500 or other degenerate case.
 * It encapsulates a ScholarScore error code, a human readable message, and an
 * HttpStatus code which is not serialized as it is redundant to the HttpStatus that
 * will be in the actual http API response to the client.
 *
 * @author markroper
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"httpStatus"})
public class StatusCode {
    private int code;
    private String message;
    private Object[] arguments;

    public StatusCode() {

    }

    public StatusCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public StatusCode(StatusCode code) {
        this.code = code.code;
        this.message = code.message;
        this.arguments = code.arguments;
    }

    public StatusCode(StatusCode code, Object[] args) {
        this.code = code.code;
        this.message = code.message;
        this.arguments = args;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object[] getArguments() {
        if (null != arguments) {
            return arguments.clone();
        } else {
            return null;
        }
    }

    public void setArguments(Object[] args) {
        if (null == args) {
            this.arguments = null;
        } else {
            this.arguments = Arrays.copyOf(args, args.length);
        }
    }


}
