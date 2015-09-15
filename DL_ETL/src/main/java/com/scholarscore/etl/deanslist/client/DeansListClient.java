package com.scholarscore.etl.deanslist.client;

import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by jwinch on 7/22/15.
 */
public class DeansListClient extends BaseHttpClient implements IDeansListClient {

    final static Logger logger = LoggerFactory.getLogger(DeansListClient.class);
    
    public static final String HEADER_LOCATION = "Location";
    
    public static final String PATH_LOGIN = "/login.php";
    // TODO: secure this when it's not just a demo account
    private static final String CREDS_PAYLOAD = "username=mroper&pw=muskrat";
    
    public static final String PATH_GET_BEHAVIOR_DATA = "/api/beta/export/get-behavior-data.php";

    private boolean authenticated = false;
    
    public DeansListClient(URI uri) {
        super(uri);
        authenticate();
    }

    @Override
    protected void authenticate() {
        
        // The only mention of login or authentication in the DeansList API document says that all requests must 
        // be accompanied by an API key, but no API key is supplied (or found in settings) with the demo account
        // we have access to. Logging in through the same endpoint as the UI (by posting credentials as the login form does)
        // does apparently allow us to make requests, though, so doing that for now.
        
        try {
            HttpPost post = new HttpPost();
            post.setHeader(new BasicHeader(HEADER_CONTENT_TYPE_NAME, HEADER_CONTENT_TYPE_X_FORM_URLENCODED));
            post.setEntity(new StringEntity(CREDS_PAYLOAD));
            post.setURI(uri.resolve(PATH_LOGIN));

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                // This is actually bad because deanslist gives us 200 but is showing us an HTML error page
                    String responseValue = EntityUtils.toString(response.getEntity());
                    logger.warn("Got (seemingly unhappy) response: " + responseValue);
                throw new DeansListClientException("Error logging into DeansList");
            } else if (response.getStatusLine().getStatusCode() == 302
                    && response.getFirstHeader(HEADER_LOCATION).getValue().contains("index.php")) {
                logger.info("Got redirected to index.php, so login was successful");
                authenticated = true;
            } else {
                authenticated = false;
                throw new HttpClientException("Failed to make request to end point: " + post.getURI() 
                        + ", status line: " + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            authenticated = false;
            throw new DeansListClientException(e);
        }

    }

    @Override
    protected Boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public BehaviorResponse getBehaviorData() {
        BehaviorResponse behaviorResponse = get(BehaviorResponse.class, PATH_GET_BEHAVIOR_DATA);
        logger.debug("got behaviorResponse: " + behaviorResponse);
        return behaviorResponse;
    }
}
