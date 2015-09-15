package com.scholarscore.api.util;

public class StatusCodes {
    public static final int OK_ERROR_CODE = 0;
    public static final int UNKNOWN_INTERNAL_ERROR_CODE = 500;
    public static final int UNPARSABLE_REQUEST_CODE = 1000;
    public static final int JSON_PARSING_ERROR_CODE = 1001;
    public static final int ENTITY_NOT_FOUND_CODE = 2000;
    public static final int UNSUPPORTED_VALUE_CODE = 3000;
    public static final int INVALID_ENTITY = 4000;
    public static final int NOT_AUTHENTICATED = 5000;

    public static StatusCode getStatusCode(StatusCodeType statusCodeType) {
        return new StatusCode(statusCodeType.getCode(), statusCodeType.getKey());
    }

    public static StatusCode getStatusCode(StatusCodeType statusCodeType, Object[] args) {
        StatusCode code = getStatusCode(statusCodeType);
        code.setArguments(args);
        return code;
    }
}
