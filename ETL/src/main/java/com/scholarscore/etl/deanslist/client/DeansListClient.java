package com.scholarscore.etl.deanslist.client;

import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.util.EdPanelDateUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jwinch on 7/22/15.
 */
public class DeansListClient extends BaseHttpClient implements IDeansListClient {

    private final static Logger logger = LoggerFactory.getLogger(DeansListClient.class);

    // instead of pulling ALL behavioral events that exist in as school's deanslist accont, we only 
    // pull ones with dates from within the lookback period. As long as sync is run successfully at least once
    // in the lookback period, all data should be migrated. Note that the sync is idempotent so MORE than one sync
    // within the lookback period is absolutely fine.
    private final int NUMBER_OF_DAYS_LOOKBACK = 90;
    
    // SimpleDateFormat is not thread-safe, so give one to each thread
    private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>(){
        @Override
        // this happens to work right now because EDPANEL_DATE_FORMAT is the same one that deanslist uses (YYYY-MM-DD)
        // but if one changes and the other doesn't, this could break
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat(EdPanelDateUtil.EDPANEL_DATE_FORMAT);
        }
    };
    
    public static final String HEADER_LOCATION = "Location";

    // TODO jordan: (deanslist) secure this when it's not just a demo account
    public static final String PATH_LOGIN = "/login.php";
    
    private String username;
    private String password;
    
    public static final String PATH_GET_BEHAVIOR_DATA = "/api/beta/export/get-behavior-data.php";

    private boolean authenticated = false;
    
    // username and password approach works, but should be switched to the APIKey approach once 
    // an APIKey can be obtained from deanslist
    public DeansListClient(URI uri, String username, String password) {
        super(uri);
        this.username = username;
        this.password = password;
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
            post.setEntity(new StringEntity(buildCredentialsEntity()));
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
        BehaviorResponse behaviorResponse = null;
        try {
            behaviorResponse = get(BehaviorResponse.class, PATH_GET_BEHAVIOR_DATA);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        logger.debug("got behaviorResponse: " + behaviorResponse);
        return behaviorResponse;
    }

    private String buildCredentialsEntity() {
        return "username=" + username + "&pw=" + password;
    }
 
    private String buildGetBehaviorUrl() { 
        Date defaultEndDate = Calendar.getInstance().getTime(); // default - get all behavioral events until now!
//        defaultEndDate  // step back 
        throw new RuntimeException("not implemented yet");
    }
    
    private String buildGetBehaviorUrlWithDates(Date behavioralEventsSince, Date behavioralEventsUntil) { 
        return PATH_GET_BEHAVIOR_DATA + "&sdt=" + getFormatter().format(behavioralEventsSince) 
                + "&edt=" + getFormatter().format(behavioralEventsUntil);
    }
    
    private SimpleDateFormat getFormatter() {
        return formatter.get();
    }

}
