package com.scholarscore.client;

import java.io.IOException;

/**
 * Created by mattg on 7/3/15.
 */
public class HttpClientException  extends IOException {
    public HttpClientException(Exception e) {
        super(e);
    }

    public HttpClientException(String msg) {
        super(msg);
    }
}
