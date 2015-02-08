package com.scholarscore.api.controller.base;
import org.apache.http.HttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;

public class NetResultActions implements ResultActions {
    private final NetMvcResult result;
    
    public NetResultActions(MockHttpServletRequest request, HttpResponse response) {
        result = new NetMvcResult(request, response);
        result.getResponse();
    }

    /**
     * NetMvcResult doesn't implement any of the required methods to enable the
     * matcher to validate HTTP status code, handler, method name, etc. so
     * attempting to validate these things will always fail. As such, this method
     * is not supported by the integration test framework. In other words, it is
     * a programming error to call this method. The framework will have to validate
     * via other means.
     *
     * @param matcher ResultMatcher
     * @return ResultActions
     * @throws Exception this method always throws an UnsupportedOperationException
     */
    @Override
    public ResultActions andExpect(ResultMatcher matcher) throws Exception {
        throw new UnsupportedOperationException("Method not supported by test framework. Do not call to validate HTTP status code, handler, method name, etc.");
    }

    @Override
    public ResultActions andDo(ResultHandler handler) throws Exception {
        handler.handle(result);
        return this;
    }

    @Override
    public MvcResult andReturn() {
        return result;
    }
}
