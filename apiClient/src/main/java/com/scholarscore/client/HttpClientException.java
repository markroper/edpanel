package com.scholarscore.client;

/**
 * Created by mattg on 7/3/15.
 */
public class HttpClientException  extends RuntimeException {
    public HttpClientException(Exception e) {
        super(e);
    }

    public HttpClientException(String msg) {
        super(msg);
    }
}
