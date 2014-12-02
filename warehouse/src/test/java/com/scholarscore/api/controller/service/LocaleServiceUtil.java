package com.scholarscore.api.controller.service;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.Assert;

import com.scholarscore.api.controller.base.IntegrationBase;

import java.util.Locale;
import java.util.Random;

/**
 * Class that contains all the methods that are used in locale definition and testing
 */
public class LocaleServiceUtil {

    private final IntegrationBase sb;
    private String [] validCharacterSet;
    private final Random rnd = new Random();

    /**
     * Constructor that takes a service base object
     * @param sb    IntegrationBase object associated with Service
     */
    public LocaleServiceUtil(IntegrationBase sb) {
        this.sb = sb;
    }

    /**
     * Description:
     * List of valid US Unicode characters used in testing
     * Expected Result:
     * Array of valid US Unicode characters
     */
    private static final String[] US_CHARS = { "\u0030", "\u0031",
            "\u0032", "\u0033", "\u0034", "\u0035", "\u0036", "\u0037", "\u0038",
            "\u0039", "\u0041", "\u0042", "\u0043", "\u0044", "\u0045", "\u0046",
            "\u0047", "\u0048", "\u0049", "\u004A", "\u004B", "\u004C", "\u004D",
            "\u004E", "\u004F", "\u0050", "\u0051", "\u0052", "\u0053", "\u0054",
            "\u0055", "\u0056", "\u0057", "\u0058", "\u0059", "\u005A", "\u0061",
            "\u0062", "\u0063", "\u0064", "\u0065", "\u0066", "\u0067", "\u0068",
            "\u0069", "\u006A", "\u006B", "\u006C", "\u006D", "\u006E", "\u006F",
            "\u0070", "\u0071", "\u0072", "\u0073", "\u0074", "\u0075", "\u0076",
            "\u0077", "\u0078", "\u0079", "\u007A"
    };

    /**
     * Description:
     * List of valid German Unicode characters used in testing
     * Expected Result:
     * Array of valid German Unicode characters
     */
    private static final String[] SPECIAL_CHARS =
            { "\u0023", "\u0024","\u0025", "\u0027", "\u002D", "\u002E", "\u002F",  };

    /**
     * Description:
     * List of valid German Unicode characters used in testing
     * Expected Result:
     * Array of valid German Unicode characters
     */
    private static final String[] GERMAN_ADDITIONAL_CHARS =
            { "\u0196", "\u0228", "\u0214", "\u0246", "\u0220", "\u0252", "\u0223", "\u0128" };

    private static final String[] GERMAN_CHARS = ArrayUtils.addAll(US_CHARS, GERMAN_ADDITIONAL_CHARS);

    /**
     * Description:
     * List of valid French Unicode characters used in testing
     * Expected Result:
     * Array of valid French Unicode characters
     */
    private static final String[] FRENCH_ADDITIONAL_CHARS = {
            "\u003F", "\u005E", "\u005F", "\u0060", "\u007B", "\u007D", "\u00A1", "\u00A2",
            "\u00A3", "\u00A4", "\u00A5", "\u00A6", "\u00A7", "\u00A8", "\u00A9", "\u00AA", "\u00AB", "\u00AC", "\u00AD",
            "\u00AE", "\u00AF", "\u00B0", "\u00B1", "\u00B2", "\u00B3", "\u00B4", "\u00B5", "\u00B6", "\u00B7", "\u00B8",
            "\u00B9", "\u00BA", "\u00BB", "\u00BC", "\u00BD", "\u00BE", "\u00BF", "\u00C0", "\u00C1", "\u00C2", "\u00C3",
            "\u00C4", "\u00C5", "\u00C6", "\u00C7", "\u00C8", "\u00C9", "\u00CA", "\u00CB", "\u00CC", "\u00CD", "\u00CE",
            "\u00CF", "\u00D0", "\u00D1", "\u00D2", "\u00D3", "\u00D4", "\u00D5", "\u00D6", "\u00D7", "\u00D8", "\u00D9",
            "\u00DA", "\u00DB", "\u00DC", "\u00DD", "\u00DE", "\u00DF", "\u00E0", "\u00E1", "\u00E2", "\u00E3", "\u00E4",
            "\u00E5", "\u00E6", "\u00E7", "\u00E8", "\u00E9", "\u00EA", "\u00EB", "\u00EC", "\u00ED", "\u00EE", "\u00EF",
            "\u00F0", "\u00F1", "\u00F2", "\u00F3", "\u00F4", "\u00F5", "\u00F6", "\u00F7", "\u00F8", "\u00F9", "\u00FA",
            "\u00FB", "\u00FC", "\u00FD", "\u00FE", "\u00FF", "\u0100", "\u0101", "\u0102", "\u0103", "\u0104", "\u0105",
            "\u0106", "\u0107", "\u0108", "\u0109", "\u010A", "\u010B", "\u010C", "\u010D", "\u010E", "\u010F", "\u0110",
            "\u0111", "\u0112", "\u0113", "\u0114", "\u0115", "\u0116", "\u0117", "\u0118", "\u0119", "\u011A", "\u011B",
            "\u011C", "\u011D", "\u011E", "\u011F", "\u0120", "\u0121", "\u0122", "\u0123", "\u0124", "\u0125", "\u0126",
            "\u0127", "\u0128", "\u0129", "\u012A", "\u012B", "\u012C", "\u012D", "\u012E", "\u012F", "\u0130", "\u0131",
            "\u0132", "\u0133", "\u0134", "\u0135", "\u0136", "\u0137", "\u0138", "\u0139", "\u013A", "\u013B", "\u013C",
            "\u013D", "\u013E", "\u013F", "\u0140", "\u0141", "\u0142", "\u0143", "\u0144", "\u0145", "\u0146", "\u0147",
            "\u0148", "\u0149", "\u014A", "\u014B", "\u014C", "\u014D", "\u014E", "\u014F", "\u0150", "\u0151", "\u0152",
            "\u0153", "\u0154", "\u0155", "\u0156", "\u0157", "\u0158", "\u0159", "\u015A", "\u015B", "\u015C", "\u015D",
            "\u015E", "\u015F", "\u0160", "\u0161", "\u0162", "\u0163", "\u0164", "\u0165", "\u0166", "\u0167", "\u0168",
            "\u0169", "\u016A", "\u016B", "\u016C", "\u016D", "\u016E", "\u016F", "\u0170", "\u0171", "\u0172", "\u0173",
            "\u0174", "\u0175", "\u0176", "\u0177", "\u0178", "\u0179", "\u017A", "\u017B", "\u017C", "\u017D", "\u017E",
            "\u017F"
     };

    private static final String [] FRENCH_CHARS = ArrayUtils.addAll(US_CHARS, FRENCH_ADDITIONAL_CHARS);


    /**
     * Description:
     * Set the Locale used for testing based on supplied command-line argument or default
     * Expected Result:
     * Locale object set for testing framework
     */
    public void setLocale() {
        String specifiedLocale = System.getProperty("locale", Locale.US.toString());
        String specifiedLang = specifiedLocale.split("_")[0];
        String specifiedCountry = specifiedLocale.split("_")[1];
        sb.locale.set(new Locale(specifiedLang, specifiedCountry));
    }

    /**
     * Description:
     * Get list of valid character set based on current Locale setting to be used in testing
     * Expected Result:
     * Array of valid Unicode characters (based on supported Locales)
     */
    String[] getLocaleCharacterSet() {
        if ( sb.locale.get().equals(Locale.US)) {
            validCharacterSet = US_CHARS;
        } else if ( sb.locale.get().equals(Locale.GERMANY)) {
            validCharacterSet = GERMAN_CHARS;
        } else if ( sb.locale.get().equals(Locale.FRANCE)) {
            validCharacterSet = FRENCH_CHARS;
        } else {
            Assert.fail("Unsupported Locale used in testing: " + sb.locale.get());
        }

        return validCharacterSet;
    }

    /**
     * Description:
     * Helper method to generate a random string from the valid character set defined by the supplied locale
     * Expected Result:
     * Random string of locale specific characters of the supplied length
     */
    public String generateString(int length) {
        validCharacterSet = getLocaleCharacterSet();
        StringBuilder genString = new StringBuilder();
        for ( int i = 0; i < length; i++) {
            int charIndex = rnd.nextInt(validCharacterSet.length);
            genString.append(validCharacterSet[charIndex]);
        }
        return genString.toString().toLowerCase();
    }

    /**
     * Description:
     * Helper method to generate a random case sensitive string from the valid character set defined by the supplied locale
     * Expected Result:
     * Random string of locale specific characters of the supplied length
     */
    public String generateCaseSensitiveString(int length) {
        validCharacterSet = getLocaleCharacterSet();
        StringBuilder genString = new StringBuilder();
        for ( int i = 0; i < length; i++) {
            int charIndex = rnd.nextInt(validCharacterSet.length);
            genString.append(validCharacterSet[charIndex]);
        }
        return genString.toString();
    }

    /**
     * Description:
     * Helper method to generate a random string from the valid character set defined by the supplied locale
     * Expected Result:
     * Random string of locale specific characters of the supplied length
     */
    public String generateSpecialCharString(int length) {
        StringBuilder genString = new StringBuilder();
        for ( int i = 0; i < length; i++) {
            int charIndex = rnd.nextInt(SPECIAL_CHARS.length);
            genString.append(SPECIAL_CHARS[charIndex]);
        }
        return genString.toString();
    }
    /**
     * Description:
     * Helper method to generate random name
     * Expected Result:
     * Create random set of characters for current locale
     */
    public String generateName() {
        return generateName(20);
    }

    /**
     * Description:
     * Helper method to generate random name
     * Expected Result:
     * Create random set of characters of the given length for current locale
     */
    public String generateName(int length) {
        return generateString(length);
    }

    /**
     * Description:
     * Helper method to generate random name that contains special characters (#, $, ., /, etc)
     * Expected Result:
     * Create random set of characters for current locale that also contains special characters
     */
    public String generateNameWithSpecialChars() {
        return generateString(5) + generateSpecialCharString(10) + generateString(5);
    }

}