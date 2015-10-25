package com.scholarscore.api.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
/**
 * This class is a non-static factory that produces a localized ErrorCode instance, given a base ErrorCode as input.
 * 
 * @author markroper
 *
 */
public class StatusCodeResponseFactory {
    public StatusCode localizeError(StatusCode code) {
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = UTF8Control.getLocalizedString(code.getMessage(), locale, code.getArguments());
        code.setMessage(localizedMessage);
        return code;
    }
}
