package com.scholarscore.etl.deanslist.client;

import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
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

    public static final String PATH_LOGIN = "/login.php";
    // TODO: secure this when it's not just a demo account
    private static final String CREDS_PAYLOAD = "username=mroper&pw=muskrat";
    
    public static final String PATH_GET_STUDENTS = "/api/beta/export/get-students.php";
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

        try {
            HttpPost post = new HttpPost();
            post.setHeader(new BasicHeader(HEADER_CONTENT_TYPE_NAME, HEADER_CONTENT_TYPE_X_FORM_URLENCODED));
            post.setEntity(new StringEntity(CREDS_PAYLOAD));
            post.setURI(uri.resolve(PATH_LOGIN));

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                    String responseValue = EntityUtils.toString(response.getEntity());
                    System.out.println("Got response: " + responseValue);
//                    return responseValue;
            } else if (response.getStatusLine().getStatusCode() == 302) {
                String redirectLocation = response.getFirstHeader("Location").getValue();
                System.out.println("Got redirected after a successful login (?) to " + redirectLocation);
            }
            else {
                throw new HttpClientException("Failed to make request to end point: " + post.getURI() 
                        + ", status line: " + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new DeansListClientException(e);
        }

    }

    @Override
    protected Boolean isAuthenticated() {
        return null;
    }

    @Override
    public void getStudents() {
        
    }

    @Override
    public void getBehaviorData() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
