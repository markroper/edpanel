package com.scholarscore.api.controller;

import com.scholarscore.api.util.StatusCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodes;

/**
 * This class catches and handles exceptions thrown within or before entering
 * other controller end point methods.
 * 
 * @author markroper
 *
 */
@ControllerAdvice
public class RestErrorHandler extends BaseController {

    @Autowired
    public RestErrorHandler(MessageSource messageSource) {
        
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        StatusCode error = new StatusCode(StatusCodes.UNPARSABLE_REQUEST_CODE, result.getFieldError().getDefaultMessage());
        return new ResponseEntity<StatusCode>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processDataIntegrationViolationException(DataIntegrityViolationException dive) {
        StatusCode error = new StatusCode(StatusCodes.DATA_INTEGRITY_VIOLATION_CODE, dive.getMessage());
        return new ResponseEntity<StatusCode>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(JsonMappingException.class) 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processJsonMappingException(JsonMappingException ex) {
        StatusCode error = new StatusCode(StatusCodes.JSON_PARSING_ERROR_CODE, ex.getMessage());
        return new ResponseEntity<StatusCode>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<StatusCode> processGeneralException(Exception ex) {
        StatusCode returnCode = StatusCodes.getStatusCode(StatusCodeType.UNKNOWN_INTERNAL_SERVER_ERROR);
        returnCode.setMessage(ex.getMessage());
        return respond(returnCode);
    }
}
