package com.scholarscore.etl.powerschool.api.auth;

/**
 * Defines the access token response from an OAuth request
 *
 * Created by mattg on 7/2/15.
 */
public class OAuthResponse {
    public Long expires_in;
    public String token_type;
    public String access_token;
}
