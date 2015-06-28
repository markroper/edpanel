/*
 * Copyright 2010 Vladimir Schaefer
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
package org.springframework.security.saml.log;

import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.util.SAMLUtil;

/**
 * Default Logger implementation sending message logs into standard Log4J logger.
 *
 * @author Vladimir Schaefer
 */
public class SAMLDefaultLogger implements SAMLLogger {

    private final static Logger log = LoggerFactory.getLogger(SAMLDefaultLogger.class);

    private boolean logMessages = false;

    public void log(String operation, String result, SAMLMessageContext context) {
        log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), null);
    }

    public void log(String operation, String result, SAMLMessageContext context, Exception e) {
        log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), e);
    }

    public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {

        if (!log.isInfoEnabled()) return;

        if (operation == null) operation = "";
        if (result == null) result = "";
        if (context == null) context = new SAMLMessageContext();

        StringBuilder sb = new StringBuilder();
        sb.append(operation);

        sb.append(";");
        sb.append(result);

        sb.append(";");
        if (context.getInboundMessageTransport() != null) {
            HTTPInTransport transport = (HTTPInTransport) context.getInboundMessageTransport();
            sb.append(transport.getPeerAddress());
        }

        if (logMessages) {
            try {
                if (context.getInboundSAMLMessage() != null) {
                    String messageStr = XMLHelper.nodeToString(SAMLUtil.marshallMessage(context.getInboundSAMLMessage()));
                    sb.append(";").append(messageStr);
                }
                if (context.getOutboundSAMLMessage() != null) {
                    String messageStr = XMLHelper.nodeToString(SAMLUtil.marshallMessage(context.getInboundSAMLMessage()));
                    sb.append(";").append(messageStr);
                }
            } catch (MessageEncodingException e1) {
                log.warn("Error marshaling message during logging", e1);
            }
        }

        log.info(sb.toString());

    }

    /**
     * @param logMessages when true whole SAML message will get logged
     */
    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

}
