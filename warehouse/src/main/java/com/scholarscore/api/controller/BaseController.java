package com.scholarscore.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorResponseFactory;

/**
 * All SpringMVC controllers defined in the package subclass this base
 * controller class, which contains utility methods used to generate error
 * and non-error API responses.
 * 
 * @author markroper
 *
 */
@Validated
public abstract class BaseController {
    
    @SuppressWarnings("unchecked")
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof ErrorCode) {
            ErrorCode err = (ErrorCode) obj;
            ErrorResponseFactory factory = new ErrorResponseFactory();
            return new ResponseEntity(factory.localizeError(err), err.getHttpStatus());
        } else {
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }
    
    protected ResponseEntity<ErrorCode> respond(ErrorCode code, Object[] args) {
        ErrorResponseFactory factory = new ErrorResponseFactory();
        ErrorCode returnError = new ErrorCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<ErrorCode>(factory.localizeError(returnError), returnError.getHttpStatus());
    }

}
