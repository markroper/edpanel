package com.scholarscore.etl.deanslist.client;

import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Created by jwinch on 7/22/15.
 */
public class DeansListClient extends BaseHttpClient implements IDeansListClient {

    public static final String HEADER_LOCATION = "Location";
    
    public static final String PATH_LOGIN = "/login.php";
    // TODO: secure this when it's not just a demo account
    private static final String CREDS_PAYLOAD = "username=mroper&pw=muskrat";
    
    public static final String PATH_GET_BEHAVIOR_DATA = "/api/beta/export/get-behavior-data.php";
    
    /*
    public static final String PATH_RESOURCE_SECTION = "/ws/v1/school/{0}/section";
    */
    
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
                    System.out.println("Got response: " + responseValue);
                throw new DeansListClientException("Error logging into DeansList");
            } else if (response.getStatusLine().getStatusCode() == 302
                    && response.getFirstHeader(HEADER_LOCATION).getValue().contains("index.php")) {
                System.out.println("Got redirected to index.php, so login was successful");
            } else {
                throw new HttpClientException("Failed to make request to end point: " + post.getURI() 
                        + ", status line: " + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new DeansListClientException(e);
        }

    }

    @Override
    protected Boolean isAuthenticated() {
        return true;
    }

    @Override
    public BehaviorResponse getBehaviorData() {
        BehaviorResponse behaviorResponse = get(BehaviorResponse.class, PATH_GET_BEHAVIOR_DATA);
        System.out.println("got behaviorResponse: " + behaviorResponse);
        return behaviorResponse;
    }
}
