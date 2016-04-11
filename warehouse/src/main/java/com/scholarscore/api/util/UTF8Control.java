package com.scholarscore.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java resource bundle files are expected to be stored in ASCII (ISO-8859-1), which causes a big problem for unicode
 * characters.  There is a Unicode alternative to storing localized strings in .properties files, using an XML format.
 * We have decided not to use this and instead to extend the Java function for loading a resource bundle such that
 * we can read in a UTF-8 file. This approach is common in the community and within Intuit.  See the links below
 * for additional background.
 *
 * To compare this overridden newBundle() method to the original view source on ResourceBundle.Control.newBundle()
 *
 * http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle
 * https://wiki.intuit.com/download/attachments/174163059/2012+QBO+i18n+-+Pete+Harris+presentation.pptx
 */
public class UTF8Control extends ResourceBundle.Control {
    private final static String BUNDLE_NAME = "localizedStrings";
    private final static String FILE_TYPE = "properties";
    private final static String ENCODING = "UTF-8";

    private final static Logger LOGGER = LoggerFactory.getLogger(UTF8Control.class);

    /**
     * Returns a ResourceBundle read in from a .properties file with UTF-8 encoding.
     * @param baseName
     * @param locale
     * @param format
     * @param loader
     * @param reload
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public ResourceBundle newBundle
            (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, FILE_TYPE);
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                // This line changes the default behavior of ResourceBundle.Control.newBundle() to support UTF-8 reads
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, ENCODING));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }

    /**
     * Given a key, locale, and replacement arguments, returns a localized String read from
     * /common/src/main/resources/localizedStrings.properties.  All localized strings from across project modules
     * are accessed via this method and the strings themselves can all be found in /common/src/main/resources/*
     * @param messageKey
     * @param currentLocale
     * @param args
     * @return
     */
    public static String getLocalizedString(String messageKey, Locale currentLocale, Object[] args) {
        //If locale is not provided, default to US.
        String localizedString = null;
        if(null != messageKey) {
            if(null == currentLocale) {
                currentLocale = Locale.US;
            }
           localizedString = getString(BUNDLE_NAME, currentLocale, messageKey, args);
        }
        return localizedString;
    }

    /**
     * Given a resource bundle key, returns a localized String value using the Spring context current locale to resolve
     * the correct language bundle.
     * @param messageKey
     * @return
     */
    public static String getLocalizedString(String messageKey) {
        //Fetch the locale from the spring context, where Spring VC will have cached it.
        String localizedString = null;
        if(null != messageKey) {
            Locale currentLocale = LocaleContextHolder.getLocale();
            localizedString = getLocalizedString(messageKey, currentLocale, null);
        }
        return localizedString;
    }

    /**
     * Given a resource bundle key, returns a number of arguments placeholders in the default locale message
     * @param messageKey
     * @return int the number of placeholder arguments, indicated by {0} {1} etc stubstring found in the raw message
     */
    public static int getNumberArgs(String messageKey) {
        int numArgs = 0;
        if(null != messageKey) {
            Locale defaultLocale = Locale.getDefault();
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, defaultLocale, new UTF8Control());
                String rawString = bundle.getString(messageKey);
                // this regex pattern looks for arg placeholders in the message
                // for example "the field {0} with type {1} is not compatible with {2}"
                // finds the 3 place holders {n}
                Pattern pattern = Pattern.compile("\\{[0-9]+\\}");
                Matcher matcher = pattern.matcher(rawString);
                //.find() checks for all occurrences of arg place holders
                while (matcher.find()) {
                    numArgs++;
                }
            } catch (MissingResourceException ex) {
                //TODO:setup logging and log here.
            }
        }
        return numArgs;
    }

    /**
     * Does the work for getting the value based on the messageKey.  Resource bundle files are named:
     * <bundleName>_<locale>.properties
     * @param bundleName - the resource bundle file
     * @param locale - locale to use.  The locale is a two char value
     * @param messageKey - key to get the value for
     * @param args - replaces {N} with arg
     * @return returns the value from the key
     */
    protected static String getString(String bundleName, Locale locale, String messageKey, Object[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, new UTF8Control());
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(locale);
        String pattern = null;
        try {
            pattern = bundle.getString(messageKey);
        } catch (MissingResourceException mre) {
            LOGGER.info("Could not load resource for messageKey " + messageKey + ", so just returning key.");
            return messageKey;
        }
        formatter.applyPattern(pattern);
        return formatter.format(args);
    }
}