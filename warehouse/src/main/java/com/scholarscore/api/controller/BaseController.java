package com.scholarscore.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.persistence.PersistenceManager;
import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorResponseFactory;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;

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
    //TODO: @mroper we need to add a real persistence layer that we call instead of manipulating this map
    public static final String JSON_ACCEPT_HEADER = "application/json";
    
    protected final PersistenceManager PM = new PersistenceManager();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof ErrorCode) {
            //If the object passed in is an error code, localize the error message to build the response
            ErrorCode err = (ErrorCode) obj;
            ErrorResponseFactory factory = new ErrorResponseFactory();
            return new ResponseEntity(factory.localizeError(err), err.getHttpStatus());
        } else if(obj instanceof ServiceResponse){
            //If the object is a ServiceResponse, resolve whether to return the ErrorCode or the value instance member
            ServiceResponse sr = (ServiceResponse) obj;
            if(null != sr.getValue()) {
                if(sr.getValue() instanceof Long) {
                    //For a long, return it as an EntityId so that serializaiton is of the form { id: <longval> }
                    return new ResponseEntity(new EntityId((Long)sr.getValue()), HttpStatus.OK);
                } else {
                    //For all other cases, just return the value
                    return new ResponseEntity(sr.getValue(), HttpStatus.OK);
                }
            } else if(null != sr.getError()){
                //Handle the error code on the service response
                return respond(sr.getError(), sr.getErrorParams());
            } else {
                //If both value and error code are null on the service response, we're dealing with a successful body-less response
                return new ResponseEntity((Object) null, HttpStatus.OK);
            }
        } 
        //If the object is neither a ServiceResponse nor an ErrorCode, respond with it directly
        return new ResponseEntity(obj, HttpStatus.OK);
    }
    
    protected ResponseEntity<ErrorCode> respond(ErrorCode code, Object[] args) {
        ErrorResponseFactory factory = new ErrorResponseFactory();
        ErrorCode returnError = new ErrorCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<ErrorCode>(factory.localizeError(returnError), returnError.getHttpStatus());
    }
}
