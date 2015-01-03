package com.scholarscore.api.util;

public class StatusCodes {
    public static final int OK_ERROR_CODE = 0;
    public static final int UNKNOWN_INTERNAL_ERROR_CODE = 500;
    public static final int UNPARSABLE_REQUEST_CODE = 1000;
    public static final int JSON_PARSING_ERROR_CODE = 1001;
    public static final int ENTITY_NOT_FOUND_CODE = 2000;
    public static final int UNSUPPORTED_VALUE_CODE = 3000;
    public static final int INVALID_ENTITY = 4000;
    
    public static final StatusCode OK = new StatusCode(OK_ERROR_CODE, null);
    
    public static final StatusCode UNKNOWN_INTERNAL_SERVER_ERROR = 
            new StatusCode(UNKNOWN_INTERNAL_ERROR_CODE, "warehouse.api.error.unknown");
    
    public static final StatusCode BAD_REQUEST_CANNOT_PARSE_BODY = 
            new StatusCode(UNPARSABLE_REQUEST_CODE, "warehouse.api.error.bodyunparsable");
    
    public static final StatusCode MODEL_NOT_FOUND = 
            new StatusCode(ENTITY_NOT_FOUND_CODE, "warehouse.api.error.model.notfound");
    
    public static final StatusCode UNSUPPORTED_ASSIGNMENT_TYPE = 
            new StatusCode(UNSUPPORTED_VALUE_CODE, "");
    
    public static final StatusCode ENTITY_INVALID_IN_CONTEXT = 
            new StatusCode(INVALID_ENTITY, "warehouse.api.error.invalidcontext");
}
