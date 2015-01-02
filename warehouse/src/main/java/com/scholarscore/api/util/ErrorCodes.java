package com.scholarscore.api.util;

import org.springframework.http.HttpStatus;

public class ErrorCodes {
    public static final Integer OK_ERROR_CODE = 0;
    public static final Integer UNKNOWN_INTERNAL_ERROR_CODE = 500;
    public static final Integer UNPARSABLE_REQUEST_CODE = 1000;
    public static final Integer JSON_PARSING_ERROR_CODE = 1001;
    public static final Integer ENTITY_NOT_FOUND_CODE = 2000;
    public static final Integer UNSUPPORTED_VALUE_CODE = 3000;
    public static final Integer INVALID_ENTITY = 4000;
    
    public static final ErrorCode OK = new ErrorCode(OK_ERROR_CODE, null, HttpStatus.OK);
    
    public static final ErrorCode UNKNOWN_INTERNAL_SERVER_ERROR = 
            new ErrorCode(UNKNOWN_INTERNAL_ERROR_CODE, "warehouse.api.error.unknown", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final ErrorCode BAD_REQUEST_CANNOT_PARSE_BODY = 
            new ErrorCode(UNPARSABLE_REQUEST_CODE, "warehouse.api.error.bodyunparsable", HttpStatus.BAD_REQUEST);
    
    public static final ErrorCode MODEL_NOT_FOUND = 
            new ErrorCode(ENTITY_NOT_FOUND_CODE, "warehouse.api.error.model.notfound", HttpStatus.NOT_FOUND);
    
    public static final ErrorCode UNSUPPORTED_ASSIGNMENT_TYPE = 
            new ErrorCode(UNSUPPORTED_VALUE_CODE, "", HttpStatus.BAD_REQUEST);
    
    public static final ErrorCode ENTITY_INVALID_IN_CONTEXT = 
            new ErrorCode(INVALID_ENTITY, "warehouse.api.error.invalidcontext", HttpStatus.BAD_REQUEST);
}
