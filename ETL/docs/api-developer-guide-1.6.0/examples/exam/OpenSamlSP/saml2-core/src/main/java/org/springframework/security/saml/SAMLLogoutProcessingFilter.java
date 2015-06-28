/* Copyright 2009 Vladimir Schäfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.saml;

import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLLogger;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter processes arriving SAML Single Logout messages by delegating to the LogoutProfile.
 *
 * @author Vladimir Schäfer
 */
public class SAMLLogoutProcessingFilter extends LogoutFilter {

    protected SAMLProcessor processor;
    protected SingleLogoutProfile logoutProfile;
    protected SAMLLogger samlLogger;
    protected SAMLContextProvider contextProvider;

    /**
     * Class logger.
     */
    protected final static Logger log = LoggerFactory.getLogger(SAMLLogoutProcessingFilter.class);

    /**
     * Default processing URL.
     */
    public static final String FILTER_URL = "/saml/SingleLogout";

    /**
     * Constructor defines URL to redirect to after successful logout and handlers.
     *
     * @param logoutSuccessUrl user will be redirected to the url after successful logout
     * @param handlers         handlers to invoke after logout
     */
    public SAMLLogoutProcessingFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
        super(logoutSuccessUrl, handlers);
        this.setFilterProcessesUrl(FILTER_URL);
    }

    /**
     * Constructor uses custom implementation for determining URL to redirect after successful logout.
     *
     * @param logoutSuccessHandler custom implementation of the logout logic
     * @param handlers             handlers to invoke after logout
     */
    public SAMLLogoutProcessingFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        super(logoutSuccessHandler, handlers);
        this.setFilterProcessesUrl(FILTER_URL);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        processLogout((HttpServletRequest) req, (HttpServletResponse) res, chain);
    }

    /**
     * Filter loads SAML message from the request object and processes it. In case the message is of LogoutResponse
     * type it is validated and user is redirected to the success page. In case the message is invalid error
     * is logged and user is redirected to the success page anyway.
     * <p/>
     * In case the LogoutRequest message is received it will be verified and local session will be destroyed.
     *
     * @param request  http request
     * @param response http response
     * @param chain    chain
     * @throws IOException      error
     * @throws ServletException error
     */
     public void processLogout(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

 		System.out.println("***********SAMLLogoutProcessingFilter.processLogout");

        if (requiresLogout(request, response)) {

            SAMLMessageContext context;

            try {

                log.debug("Processing SAML2 logout message");
                context = contextProvider.getLocalEntity(request, response);
                context.setCommunicationProfileId(getProfileName());
                processor.retrieveMessage(context);
                context.setLocalEntityEndpoint(SAMLUtil.getEndpoint(context.getLocalEntityRoleMetadata().getEndpoints(), context.getInboundSAMLBinding(), getFilterProcessesUrl()));

            } catch (SAMLException e) {
                throw new SAMLRuntimeException("Incoming SAML message is invalid", e);
            } catch (MetadataProviderException e) {
                throw new SAMLRuntimeException("Error determining metadata contracts", e);
            } catch (MessageDecodingException e) {
                throw new SAMLRuntimeException("Error decoding incoming SAML message", e);
            } catch (org.opensaml.xml.security.SecurityException e) {
                throw new SAMLRuntimeException("Incoming SAML message is invalid", e);
            }

            boolean doLogout = true;

            if (context.getInboundSAMLMessage() instanceof LogoutResponse) {

                try {
                    logoutProfile.processLogoutResponse(context);
                    samlLogger.log(SAMLConstants.LOGOUT_RESPONSE, SAMLConstants.SUCCESS, context);
                } catch (Exception e) {
                    samlLogger.log(SAMLConstants.LOGOUT_RESPONSE, SAMLConstants.FAILURE, context);
                    log.warn("Received global logout response is invalid", e);
                }

            } else if (context.getInboundSAMLMessage() instanceof LogoutRequest) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                SAMLCredential credential = null;
                if (auth != null) {
                    credential = (SAMLCredential) auth.getCredentials();
                }

                try {
                    // Process request and send response to the sender in case the request is valid
                    doLogout = logoutProfile.processLogoutRequest(context, credential);
                    samlLogger.log(SAMLConstants.LOGOUT_REQUEST, SAMLConstants.SUCCESS, context);
                } catch (Exception e) {
                    samlLogger.log(SAMLConstants.LOGOUT_REQUEST, SAMLConstants.FAILURE, context);
                    log.warn("Received global logout request is invalid", e);
                }

            }

            if (doLogout) {
                super.doFilter(request, response, chain);
            }

        } else {
            chain.doFilter(request, response);
        }

    }

    /**
     * Name of the profile processed by this class.
     *
     * @return profile name
     */
    protected String getProfileName() {
        return SAMLConstants.SAML2_SLO_PROFILE_URI;
    }

    /**
     * The filter will be used in case the URL of the request contains the DEFAULT_FILTER_URL.
     *
     * @param request request used to determine whether to enable this filter
     * @return true if this filter should be used
     */
    @Override
    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        return SAMLUtil.processFilter(getFilterProcessesUrl(), request);
    }

    @Override
    public String getFilterProcessesUrl() {
        return super.getFilterProcessesUrl();
    }

    /**
     * Object capable of parse SAML messages from requests, must be set.
     *
     * @param processor processor
     */
    @Autowired
    public void setSAMLProcessor(SAMLProcessor processor) {
        Assert.notNull(processor, "SAML Processor can't be null");
        this.processor = processor;
    }

    /**
     * Profile for consumption of processed messages, must be set.
     *
     * @param logoutProfile profile
     */
    @Autowired
    public void setLogoutProfile(SingleLogoutProfile logoutProfile) {
        Assert.notNull(logoutProfile, "SingleLogoutProfile can't be null");
        this.logoutProfile = logoutProfile;
    }

    /**
     * Logger for SAML events, must be set.
     *
     * @param samlLogger logger
     */
    @Autowired
    public void setSamlLogger(SAMLLogger samlLogger) {
        Assert.notNull(samlLogger, "SAML logger can't be null");
        this.samlLogger = samlLogger;
    }

    /**
     * Sets entity responsible for populating local entity context data. Must be set.
     *
     * @param contextProvider provider implementation
     */
    @Autowired
    public void setContextProvider(SAMLContextProvider contextProvider) {
        Assert.notNull(contextProvider, "Context provider can't be null");
        this.contextProvider = contextProvider;
    }

    /**
     * Verifies that required entities were autowired or set.
     *
     * @throws ServletException
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(processor, "SAMLProcessor must be set");
        Assert.notNull(contextProvider, "Context provider must be set");
        Assert.notNull(logoutProfile, "Logout profile must be set");
        Assert.notNull(samlLogger, "SAML Logger must be set");
    }

}