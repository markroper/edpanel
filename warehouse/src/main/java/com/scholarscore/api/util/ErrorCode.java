package com.scholarscore.api.util;

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
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties( {"httpStatus"} )
public class ErrorCode {
	private long code;
	private String message;
	@XmlTransient
	HttpStatus httpStatus;
	
	public ErrorCode() {
		
	}
	
	public ErrorCode(long code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.httpStatus = status;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
