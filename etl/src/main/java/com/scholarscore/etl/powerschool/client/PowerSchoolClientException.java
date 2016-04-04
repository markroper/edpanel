package com.scholarscore.etl.powerschool.client;

/**
 * Created by mattg on 7/2/15.
 */
public class PowerSchoolClientException extends RuntimeException {
    public PowerSchoolClientException(Exception e) {
        super(e);
    }

    public PowerSchoolClientException(String msg) {
        super(msg);
    }
    
    public PowerSchoolClientException(String msg, Throwable throwable) { super(msg, throwable); }
}
