package com.scholarscore.etl.schoolbrains;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 4/14/16.
 */
public abstract class BaseParser<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseParser.class);
    protected File input;
    protected CSVParser parser;
    public BaseParser(File file) {
        this.input = file;
    }
    protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");

    public abstract T parseRec(CSVRecord rec);

    public List<T> parse() {
        if(null == input) {
            return null;
        }
        try {
            List<T> results = new ArrayList<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for (CSVRecord rec : parser) {
               results.add(parseRec(rec));
            }
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse CSV file. " + e.getMessage());
            return null;
        }
    }

    public Double parseDoubleOrReturnNull(String input) {
        try {
            return Double.parseDouble(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse double from input: " + input);
        }
        return null;
    }
    public Long parseLongOrReturnNull(String input) {
        try {
            return Long.parseLong(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse long from input: " + input);
        }
        return null;
    }
}
