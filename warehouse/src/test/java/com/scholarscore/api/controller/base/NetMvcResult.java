package com.scholarscore.api.controller.base;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * NetMvcResult is an encapsulation of the mock request, the mock response and the http component response received
 * from the server.  These elements are cached in this object.  The mock response is generated in this class by
 * converting the contents of the http component response into a mock http response object.
 */
public class NetMvcResult implements MvcResult {
    private final MockHttpServletRequest request;
    private MockHttpServletResponse mockResponse;
    private final HttpResponse response;

    public NetMvcResult(MockHttpServletRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public MockHttpServletRequest getRequest() {
        return request;
    }

    @Override
    public synchronized MockHttpServletResponse getResponse() {
        // conversion needs to happen just once
        if (null == mockResponse) {
            mockResponse = convertResponseToMockResponse(response);
        }
        return mockResponse;
    }

    private MockHttpServletResponse convertResponseToMockResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        // Set the content response
        try {
            byte[] contentBytes = IOUtils.toByteArray(entity.getContent());
            mockResponse.setOutputStreamAccessAllowed(true);
            mockResponse.getOutputStream().write(contentBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set content length, status code, and headers
        mockResponse.setContentLength((int)response.getEntity().getContentLength());
        mockResponse.setStatus(response.getStatusLine().getStatusCode());
        for (Header header : response.getAllHeaders()) {
            mockResponse.setHeader(header.getName(), header.getValue());
        }

        return mockResponse;
    }

    @Override
    public Object getHandler() {
        return null;
    }

    @Override
    public HandlerInterceptor[] getInterceptors() {
        return new HandlerInterceptor[0];
    }

    @Override
    public ModelAndView getModelAndView() {
        return null;
    }

    @Override
    public Exception getResolvedException() {
        return null;
    }

    @Override
    public FlashMap getFlashMap() {
        return null;
    }

    @Override
    public Object getAsyncResult() {
        return null;
    }

    @Override
    public Object getAsyncResult(long timeToWait) {
        return null;
    }
}
