package com.scholarscore.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.models.serializers.ObjectParsingException;

/**
 * This class catches and handles exceptions thrown within or before entering
 * other controller endpoints methods.
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
    public ResponseEntity<ErrorCode> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        ErrorCode error = new ErrorCode(ErrorCodes.UNPARSABLE_REQUEST_CODE, result.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<ErrorCode>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(JsonMappingException.class) 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorCode> processJsonMappingException(JsonMappingException ex) {
        ErrorCode error = new ErrorCode(ErrorCodes.JSON_PARSING_ERROR_CODE, ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<ErrorCode>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ObjectParsingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorCode> processAppJsonMappingException(ObjectParsingException ex) {
        ErrorCode returnCode = new ErrorCode(ErrorCodes.UNSUPPORTED_ASSIGNMENT_TYPE);
        returnCode.setMessage(ex.getMessage());
        return respond(returnCode, ex.getArgs());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorCode> processGeneralException(Exception ex) {
        return respond(ErrorCodes.UNKNOWN_INTERNAL_SERVER_ERROR, new Object[]{});
    }
}
