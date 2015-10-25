package com.scholarscore.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Defines a common pattern for interaction with a JSON http end point
 *
 * Created by mattg on 7/3/15.
 */
public abstract class BaseHttpClient {
    protected static final String HEADER_CONTENT_TYPE_NAME = "Content-Type";
    protected static final String HEADER_CONTENT_TYPE_X_FORM_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";
    protected static final String HEADER_ACCEPT_JSON_VALUE = "application/json";
    protected static final String HEADER_ACCEPT_NAME = "Accept";
    protected static final Header HEADER_CONTENT_TYPE_JSON = new BasicHeader("Content-Type", "application/json");
    protected static final Header HEADER_ACCEPT_JSON = new BasicHeader(HEADER_ACCEPT_NAME, HEADER_ACCEPT_JSON_VALUE);
    protected static final int CONNECTION_TIMEOUT = 3000;
    protected static final int CONNECTION_REQUEST_TIMEOUT = 30000;

    protected final CloseableHttpClient httpclient;
    protected final URI uri;

    protected Gson gson;

    public BaseHttpClient(URI uri) {
        this.uri = uri;
        this.httpclient = createClient();
        this.gson = createGsonParser();
    }

    protected Gson createGsonParser() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    protected CloseableHttpClient createClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            RequestConfig requestConfig = RequestConfig.custom().
                    setConnectTimeout(CONNECTION_TIMEOUT).
                    setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).
                    setSocketTimeout(CONNECTION_REQUEST_TIMEOUT).build();
            return HttpClients.custom().
                    setSSLSocketFactory(sslsf).
                    setDefaultRequestConfig(requestConfig).
                    build();
        }
        catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new HttpClientException(e);
        }
    }

    protected String post(byte[] data, String path) throws IOException {
        String strData = new String(data);
        HttpPost post = new HttpPost();
        post.setURI(uri.resolve(path));
        setupCommonHeaders(post);
        post.setHeader(HEADER_CONTENT_TYPE_JSON);
        post.setEntity(new ByteArrayEntity(data));
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
            int code = response.getStatusLine().getStatusCode();
            String json = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_CREATED || code == HttpStatus.SC_OK) {
                return json;
            } else {
                throw new HttpClientException("Failed to post to end point: " + post.getURI().toString() + ", status line: " + response.getStatusLine().toString() + ", payload: " + json);
            }
        } catch (IOException e) {
            throw new HttpClientException(e);
        } finally {
            response.getEntity().getContent().close();
        }
    }

    protected String getPath(String root, String ...params) {
        String path = root;
        if (null != params && params.length > 0) {
            int count = 0;
            for (String param : params) {
                path = path.replaceAll("\\{" + count + "\\}", param);
                count++;
            }
        }
        return path;
    }

    /**
     * PowerSchool inexplicably shits the bed on some perfectly valid and simple API calls.  Not clear if
     * this is rate limiting or throttling or if this is just the ghost in the machine.  For this reason,
     * this method provides a retry mechanism, recursively calling itself in the event of timeout until
     * retries < 0, at which point it throws a new HttpClientException.
     *
     * @param clazz
     * @param path
     * @param retries
     * @param params
     * @param <T>
     * @return
     */
    protected <T> T get(Class<T> clazz, String path, int retries, String ...params) {
        if(retries < 0) {
            throw new HttpClientException("Retry limit exceeded for API call with URI: " + path);
        }
        try {
            return get(clazz, path, params);
        } catch(HttpClientException e) {
            return get(clazz, path, retries - 1, params);
        }
    }
    protected <T> T get(Class<T> clazz, String path, String ...params) {

        path = getPath(path, params);

        try {
            HttpGet get = new HttpGet();
            setupCommonHeaders(get);
            get.setURI(uri.resolve(path));
            String json = getJSON(get);
            return gson.fromJson(json, clazz);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    protected String patch(byte[] data, String path) throws IOException {
        HttpPatch patch = new HttpPatch();
        patch.setURI(uri.resolve(path));
        setupCommonHeaders(patch);
        patch.setHeader(HEADER_CONTENT_TYPE_JSON);
        patch.setEntity(new ByteArrayEntity(data));
        HttpResponse response = null;
        try {
            response = httpclient.execute(patch);
            int code = response.getStatusLine().getStatusCode();
            String json = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_CREATED || code == HttpStatus.SC_OK) {
                return json;
            }
            else {
                throw new HttpClientException("Failed to post to end point: " + patch.getURI().toString() + ", status line: " + response.getStatusLine().toString() + ", payload: " + json);
            }
        } catch (IOException e) {
            throw new HttpClientException(e);
        } finally {
            response.getEntity().getContent().close();
        }
    }

    protected String getJSON(HttpUriRequest request) throws IOException {
        HttpResponse response = httpclient.execute(request);
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                if (null != response) {
                    String responseValue = EntityUtils.toString(response.getEntity());
                    return responseValue;
                }
            } else {
                throw new HttpClientException("Failed to make request to end point: " + request.getURI() + ", status line: " + response.getStatusLine().toString());
            }
        } finally {
            response.getEntity().getContent().close();
        }
        return null;
    }

    protected void setupCommonHeaders(HttpRequest request) {
        request.setHeader(HEADER_ACCEPT_JSON);
    }
    protected abstract void authenticate();
    protected abstract Boolean isAuthenticated();
}
