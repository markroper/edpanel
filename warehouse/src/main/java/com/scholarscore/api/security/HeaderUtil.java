package com.scholarscore.api.security;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class HeaderUtil {
    private boolean encryptionEnabled;
    private EncryptionUtil encryptionUtil = new EncryptionUtil();
    private static final String HEADER_NAME = "X-Auth-Token";
    private Period sessionMaxAge;
    private String seed;

    public HeaderUtil(String encryptionEnabled, String seed, String sessionMaxAge) {
        this.encryptionEnabled = Boolean.parseBoolean(encryptionEnabled);
        this.seed = seed;
        setSessionMaxAge(sessionMaxAge);
        if(this.encryptionEnabled) {
            encryptionUtil.encryptionEnabled();
        }
    }

    public String getUserName(HttpServletRequest request) {
        String header = request.getHeader(HEADER_NAME);
        return StringUtils.isNotBlank(header) ? extractUserName(header) : null;
    }

    private String extractUserName(String value) {

        try {
            String decryptedValue = encryptionUtil.decrypt(value, seed);
            String[] split = decryptedValue.split("\\|");
            String username = split[0];
            DateTime timestamp =  new DateTime(Long.parseLong(split[1]));
            if (timestamp.isAfter(DateTime.now().minus(sessionMaxAge))) {
                return username;
            }
        } catch (IOException | GeneralSecurityException e) {
            //LOG.debug("Unable to decrypt header", e);
        }
        return null;
    }

    public void addHeader(HttpServletResponse response, String userName) {
        try {
            String encryptedValue = createAuthToken(userName);
            response.setHeader(HEADER_NAME, encryptedValue);
        } catch (IOException | GeneralSecurityException e) {
            //LOG.error("Unable to encrypt header", e);
        }
    }

    public String createAuthToken(String userName) throws IOException, GeneralSecurityException {
        String value = userName + "|" + System.currentTimeMillis();
        return encryptionUtil.encrypt(value, seed);
    }

    public void setSessionMaxAge(String max) {
        PeriodFormatter format = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d", "d")
                .printZeroRarelyFirst()
                .appendHours()
                .appendSuffix("h", "h")
                .printZeroRarelyFirst()
                .appendMinutes()
                .appendSuffix("m", "m")
                .toFormatter();
        Period sessionMaxAge = format.parsePeriod(max);
        this.sessionMaxAge = sessionMaxAge;
    }
    
    public Period getSessionMaxAge() {
        return sessionMaxAge;
    }

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
    
    
}
