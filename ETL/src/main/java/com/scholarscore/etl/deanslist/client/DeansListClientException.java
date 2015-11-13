package com.scholarscore.etl.deanslist.client;

/**
 * Created by jwinch on 7/23/15.
 */
public class DeansListClientException extends RuntimeException {

    public DeansListClientException(Exception e) { 
        super(e);
    }
    
    public DeansListClientException(String msg) {
        super(msg);
    }
}

