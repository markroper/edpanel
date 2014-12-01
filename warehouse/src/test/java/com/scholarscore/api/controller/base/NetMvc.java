package com.scholarscore.api.controller.base;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.testng.Assert.fail;

/**
 * This is a network implementation of MockMVC instead of hitting the controller directly this implementation
 * will use httpcomponents to make the network call and covert the responses into mock responses.  This way our
 * functional tests can be both functional (internal calls) and external network calls thus become function to
 * integration test cases that will hit the nginx server and respond appropriately acting as a client.
 *
 */
public class NetMvc {

    private static final String CHARSET_UTF8_NAME = "UTF-8";

    /**
     * Executes the HTTP request against the specified endpoint and returns the
     * server response. Use this overload if the request has no content body.
     *
     * @param requestBuilder request to execute
     * @return server response
     */
    public ResultActions perform(RequestBuilder requestBuilder) {
        return perform(requestBuilder, null);
    }

    /**
     * Executes the HTTP request against the specified endpoint and returns the
     * server response.
     *
     * @param requestBuilder request to execute
     * @param content content to add to the request (or null if no content)
     * @return server response
     */
    public ResultActions perform(RequestBuilder requestBuilder, byte[] content) {
        MockHttpServletRequest mockRequest = requestBuilder.buildRequest(new MockServletContext("/warehouse"));

        ResultActions resultActions = null;
        try {
            HttpUriRequest httpRequest;
            URI uri = buildUri(mockRequest);

            String httpMethod = mockRequest.getMethod().toUpperCase().trim();

            // Create the HTTP request
            switch (httpMethod) {
                case HttpPost.METHOD_NAME:
                    httpRequest = new HttpPost(uri);
                    break;
                case HttpGet.METHOD_NAME:
                    httpRequest = new HttpGet(uri);
                    break;
                case HttpPatch.METHOD_NAME:
                    httpRequest = new HttpPatch(uri);
                    break;
                case HttpPut.METHOD_NAME:
                    httpRequest = new HttpPut(uri);
                    break;
                case HttpDelete.METHOD_NAME:
                    httpRequest = new HttpDelete(uri);
                    break;
                default:
                    throw new IllegalStateException("Unsupported HTTP method: " + httpMethod);
            }

            // Fill in the request with headers, content, and multi-part file
            addHeaders(httpRequest, mockRequest);
            addContent(httpRequest, content);
            addMultipartFile(httpRequest, mockRequest);

            // Execute the request
            HttpClient defaultHttpClient = getHttpClient();
            HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);

            // Return the results
            resultActions = new NetResultActions(mockRequest, httpResponse);
        } catch (Exception e) {
            //LOGGER.sys().error("Unexpected exception occurred attempting to make request", e);
            fail("Unexpected exception occurred attempting to make request", e);
        }
        return resultActions;
    }

    /**
     * Builds the URI to use for constructing HttpRequests.
     *
     * @param mockRequest Spring mock request containing the endpoint info
     * @return URI pointing at the specified endpoint
     * @throws URISyntaxException if the URI data in the mock request is malformed
     */
    private URI buildUri(MockHttpServletRequest mockRequest) throws URISyntaxException {
        // Build the list of query params
        List<NameValuePair> reqParams = new ArrayList<>();;
        Map<String, String[]> paramsMap = mockRequest.getParameterMap();
        if (null != paramsMap) {
            Map<String,String[]> parameters = paramsMap;
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                reqParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()[0]));
            }
        }

        // Build the URI from the mock request
        URI uri = new URIBuilder()
                .setScheme(mockRequest.getScheme())
                .setHost(mockRequest.getServerName())
                .setPort(mockRequest.getServerPort())
                .setPath(mockRequest.getRequestURI())
                .addParameters(reqParams)
                .build();

        return uri;
    }

    /**
     * Adds all the headers from the MockHttpServletRequest to the HttpRequest.
     *
     * @param httpRequest HttpRequest on which to add the headers
     * @param mockRequest Spring mock request containing the headers to add
     */
    private void addHeaders(HttpUriRequest httpRequest, MockHttpServletRequest mockRequest) {
        // Add header information
        if (null != mockRequest.getHeaderNames()) {
            Enumeration<String> names = mockRequest.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                httpRequest.setHeader(name, mockRequest.getHeader(name));
            }
        }
    }

    /**
     * Adds the content entity to the HttpRequest if the HttpRequest is the type
     * of the request that can contain an entity.
     *
     * @param httpRequest HttpRequest on which to add the content
     * @param content content to add
     * @throws UnsupportedEncodingException if an error occurs converting the
     * content to a UTF-8 string
     */
    private void addContent(HttpUriRequest httpRequest, byte[] content)
            throws UnsupportedEncodingException {
        // Only add the content if the HttpRequest supports it
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) httpRequest;

            // Add the content to the request
            if (null != content && content.length > 0) {
                entityEnclosingRequest.setEntity(new ByteArrayEntity(content));
                String body = new String(content, CHARSET_UTF8_NAME);
                //LOGGER.sys().info("Request BODY: " + body);
            }
        }
    }

    /**
     * Adds the multi-part file to the HttpRequest if the HttpRequest is the type
     * of request that can contain an entity.
     *
     * @param httpRequest HttpRequest on which to add the multi-part file
     * @param mockRequest Spring mock request containing the multi-part file
     * @throws java.io.IOException if an error occurs reading the multi-part file
     */
    private void addMultipartFile(HttpUriRequest httpRequest, MockHttpServletRequest mockRequest)
            throws IOException {
        // Only add the multi-part file if the HttpRequest supports it
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) httpRequest;

            // Add the multi-part file to the request
            if (mockRequest instanceof MockMultipartHttpServletRequest) {
                // Remove the existing Content-Type header. This header is currently
                // set to "multipart/form-data" but does not contain the "boundary"
                // which the server needs to be able to split the request. Without
                // the "boundary", this request will fail with HTTP status code 500.
                //
                // If we leave the Content-Type header absent, the HttpComponents
                // library will auto-magically rewrite it for us to include this
                // boundary and the request will succeed.
                httpRequest.removeHeaders("Content-Type");

                MockMultipartHttpServletRequest multipartHttpServletRequest =
                        (MockMultipartHttpServletRequest) mockRequest;
                Iterator<String> fileNameIterator = multipartHttpServletRequest.getFileNames();
                MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                // Add the part for each file
                while (fileNameIterator.hasNext()) {
                    String fileName = fileNameIterator.next();
//                    LOGGER.sys().info("Multipart fileName={}  contentType={}  size={}", fileName,
//                            multipartHttpServletRequest.getMultipartContentType(fileName),
//                            multipartHttpServletRequest.getFile(fileName).getSize());
                    InputStreamBody inputStreamBody = new InputStreamBody(
                            multipartHttpServletRequest.getFile(fileName).getInputStream(),
                            ContentType.create(multipartHttpServletRequest.getMultipartContentType(fileName)),
                            fileName);
                    multipartEntity.addPart("file", inputStreamBody);
                }
                entityEnclosingRequest.setEntity(multipartEntity.build());
            }
        }
    }

    /**
     * Get an instance of HttpClient suitable for executing HTTP requests
     * against the integration test environment. Uses SSL but trusts all
     * certificates and allows all hostnames.
     *
     * @return default HttpClient for testing
     * @throws Exception if an error occurs instantiating the HttpClient
     */
    public HttpClient getHttpClient() throws Exception {
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
                    }
                    @Override
                    public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);

        // Check for proxy system properties.
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");

        // Build the HttpClient, optionally including the proxy
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(cm);
        if (null != proxyHost && null != proxyPort) {
            int port = Integer.parseInt(proxyPort);
            HttpHost proxy = new HttpHost(proxyHost, port);
            builder.setProxy(proxy);
        }
        return builder.build();
    }
}