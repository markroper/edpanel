package com.scholarscore.etl.powerschool.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scholarscore.etl.powerschool.api.auth.OAuthResponse;
import com.scholarscore.etl.powerschool.api.model.District;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mattg on 7/2/15.
 */
public class PowerSchoolClient implements IPowerSchoolClient {

    private static final String HEADER_ACCEPT_NAME = "Accept";
    private static final String HEADER_AUTH_NAME = "Authorization";

    private static final String PATH_RESOURCE_DISTRICT = "/ws/v1/district";
    private static final String PATH_RESOURCE_SCHOOL = "/ws/v1/district/school";

    private static final String HEADER_CONTENT_TYPE_NAME = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_X_FORM_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";

    private static final String HEADER_ACCEPT_JSON = "application/json";
    private static final String GRANT_TYPE_CREDS = "grant_type=client_credentials";
    private static final String URI_PATH_OATH = "/oauth/access_token";
    private final String clientSecret;
    private final String clientId;
    private final URI uri;

    private OAuthResponse oauthToken;
    private final CloseableHttpClient httpclient;
    private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    public PowerSchoolClient(String clientId, String clientSecret, URI uri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpclient = HttpClients.createDefault();
        this.uri = uri;
        authenticate();
    }

    public void authenticate() {
        try {
            HttpPost post = new HttpPost();
            post.setHeader(new BasicHeader(HEADER_CONTENT_TYPE_NAME, HEADER_CONTENT_TYPE_X_FORM_URLENCODED));
            post.setEntity(new StringEntity(GRANT_TYPE_CREDS));
            setupCommonHeaders(post);
            post.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(clientId, clientSecret),
                    "UTF-8", false));
            post.setURI(uri.resolve(URI_PATH_OATH));
            String json = getJSON(post);
            if (null != json) {
                oauthToken = gson.fromJson(json, OAuthResponse.class);
                if (null == oauthToken || null == oauthToken.access_token) {
                    throw new PowerSchoolClientException("Unable to authenticate with power school, response: " + json);
                }
            }
        } catch (IOException e) {
            throw new PowerSchoolClientException(e);
        }
    }

    @Override
    public SchoolsResponse getSchools() {
        return get(SchoolsResponse.class, PATH_RESOURCE_SCHOOL);
    }

    @Override
    public DistrictResponse getDistrict() {
        return get(DistrictResponse.class, PATH_RESOURCE_DISTRICT);
    }

    private <T> T get(Class<T> clazz, String path) {
        try {
            HttpGet get = new HttpGet();
            setupCommonHeaders(get);
            get.setURI(uri.resolve(path));
            String json = getJSON(get);
            return gson.fromJson(json, clazz);
        } catch (IOException e) {
            throw new PowerSchoolClientException(e);
        }
    }

    protected String getJSON(HttpUriRequest request) throws IOException {
        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            if (null != response) {
                String responseValue = EntityUtils.toString(response.getEntity());
                System.out.println("Response from: " + request.getURI() + "\nValue: " + responseValue);
                return responseValue;
            }
        }
        else {
            throw new PowerSchoolClientException("Failed to make request to end point: " + request.getURI() + ", status line: " + response.getStatusLine().toString());
        }
        return null;
    }

    public void setupCommonHeaders(HttpRequest req) {
        req.setHeader(new BasicHeader(HEADER_ACCEPT_NAME, HEADER_ACCEPT_JSON));

        if (null != oauthToken) {
            req.setHeader(new BasicHeader(HEADER_AUTH_NAME, oauthToken.token_type + " " + oauthToken.access_token));
        }
    }
}
