package com.scholarscore.etl.schoolbrains.parser;

import com.scholarscore.models.user.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
public abstract class BaseParser<T> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(BaseParser.class);
    protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");

    protected CSVParser parser;
    
    public abstract T parseRec(CSVRecord rec);

    public Set<T> parse(File input) {
        int numRecs = 0;
        int dupes = 0;
        if(null == input) {
            return null;
        }
        try {
            Set<T> results = new HashSet<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for (CSVRecord rec : parser) {
                numRecs++;
                T item = parseRec(rec);
                if(null != item) {
                    T parsedRecord = parseRec(rec);
                    if (results.contains(parsedRecord)) {
                        dupes++;
                        if (parsedRecord instanceof Person) {
                            LOGGER.warn("In " + getClass().getSimpleName() + " parser, inserting already-found record with SSID:" + ((Person)parsedRecord).getSourceSystemId() 
                                    +"," + "" + "!");
                        } else {
                            LOGGER.warn("In " + getClass().getSimpleName() + " parser, inserting already-found record!");
                        }
                    }
                    results.add(parsedRecord);
                } else {
                    LOGGER.warn("got null item...");
                }
            }
            LOGGER.warn("num recs: " + numRecs + ", dupes: " + dupes);
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse CSV file. " + e.getMessage());
            return null;
        }
    }

    public static Double parseDoubleOrReturnNull(String input) {
        try {
            return Double.parseDouble(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse double from input: " + input);
        }
        return null;
    }
    public static Long parseLongOrReturnNull(String input) {
        try {
            return Long.parseLong(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse long from input: " + input);
        }
        return null;
    }
}
