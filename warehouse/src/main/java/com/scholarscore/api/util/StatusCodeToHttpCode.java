package com.scholarscore.api.util;

import org.springframework.http.HttpStatus;

public class StatusCodeToHttpCode {

    /**
     * Given a systemCode that is particular to the platform, 
     * resolves and returns an HttpStatus code.
     * 
     * @param systemCode
     * @return
     */
    public static HttpStatus resolveHttpStatus(int systemCode) {
        HttpStatus status = HttpStatus.OK;
        switch(systemCode) {
            case StatusCodes.OK_ERROR_CODE:
                status = HttpStatus.OK;
                break;
            case StatusCodes.ENTITY_NOT_FOUND_CODE:
                status = HttpStatus.NOT_FOUND;
                break;
            case StatusCodes.INVALID_ENTITY:
            case StatusCodes.JSON_PARSING_ERROR_CODE:
            case StatusCodes.UNPARSABLE_REQUEST_CODE:
            case StatusCodes.UNSUPPORTED_VALUE_CODE:
                status = HttpStatus.BAD_REQUEST;
                break;
            case StatusCodes.UNKNOWN_INTERNAL_ERROR_CODE:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            default:
                break;   
        }
        return status;
    }
}
