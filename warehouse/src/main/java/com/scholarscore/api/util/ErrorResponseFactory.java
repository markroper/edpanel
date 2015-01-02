package com.scholarscore.api.util;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
/**
 * This class is a non-static factory that produces a localized ErrorCode instance, given a base ErrorCode as input.
 * 
 * @author markroper
 *
 */
public class ErrorResponseFactory {
    public ErrorCode localizeError(ErrorCode code) {
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = UTF8Control.getLocalizedString(code.getMessage(), locale, code.getArguments());
        ErrorCode returnCode = new ErrorCode(code.getCode(), localizedMessage, code.getHttpStatus());
        return returnCode;
    }
}
