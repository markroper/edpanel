package com.scholarscore.api.util;

import org.springframework.http.HttpStatus;

public class StatusCodes {
    public static final Integer OK_ERROR_CODE = 0;
    public static final Integer UNKNOWN_INTERNAL_ERROR_CODE = 500;
    public static final Integer UNPARSABLE_REQUEST_CODE = 1000;
    public static final Integer JSON_PARSING_ERROR_CODE = 1001;
    public static final Integer ENTITY_NOT_FOUND_CODE = 2000;
    public static final Integer UNSUPPORTED_VALUE_CODE = 3000;
    public static final Integer INVALID_ENTITY = 4000;
    
    public static final StatusCode OK = new StatusCode(OK_ERROR_CODE, null, HttpStatus.OK);
    
    public static final StatusCode UNKNOWN_INTERNAL_SERVER_ERROR = 
            new StatusCode(UNKNOWN_INTERNAL_ERROR_CODE, "warehouse.api.error.unknown", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final StatusCode BAD_REQUEST_CANNOT_PARSE_BODY = 
            new StatusCode(UNPARSABLE_REQUEST_CODE, "warehouse.api.error.bodyunparsable", HttpStatus.BAD_REQUEST);
    
    public static final StatusCode MODEL_NOT_FOUND = 
            new StatusCode(ENTITY_NOT_FOUND_CODE, "warehouse.api.error.model.notfound", HttpStatus.NOT_FOUND);
    
    public static final StatusCode UNSUPPORTED_ASSIGNMENT_TYPE = 
            new StatusCode(UNSUPPORTED_VALUE_CODE, "", HttpStatus.BAD_REQUEST);
    
    public static final StatusCode ENTITY_INVALID_IN_CONTEXT = 
            new StatusCode(INVALID_ENTITY, "warehouse.api.error.invalidcontext", HttpStatus.BAD_REQUEST);
}
