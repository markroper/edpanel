package com.scholarscore.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * This class catches and handles exceptions thrown within or before entering
 * other controller end point methods.
 * 
 * @author markroper
 *
 */
@ControllerAdvice
public class RestErrorHandler extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);
    
    @Autowired
    public RestErrorHandler(MessageSource messageSource) { }
    
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        StatusCode error = new StatusCode(StatusCodes.UNPARSABLE_REQUEST_CODE, result.getFieldError().getDefaultMessage() + " (field:) " + result.getFieldError().getField());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processDataIntegrationViolationException(DataIntegrityViolationException dive) {
        StatusCode error = new StatusCode(StatusCodes.DATA_INTEGRITY_VIOLATION_CODE, dive.getRootCause().getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(JsonMappingException.class) 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processJsonMappingException(JsonMappingException ex) {
        StatusCode error = new StatusCode(StatusCodes.JSON_PARSING_ERROR_CODE, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<StatusCode> processConstraintViolationException(ConstraintViolationException ex) { 
        StatusCode error = new StatusCode(StatusCodes.CONSTRAINT_VIOLATED_ERROR_CODE, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<StatusCode> processGeneralException(Exception ex) {
        LOGGER.warn("UNHANDLED EXCEPTION!", ex);
        StatusCode returnCode = StatusCodes.getStatusCode(StatusCodeType.UNKNOWN_INTERNAL_SERVER_ERROR);
        returnCode.setMessage(ex.getMessage());
        return respond(returnCode);
    }
}
