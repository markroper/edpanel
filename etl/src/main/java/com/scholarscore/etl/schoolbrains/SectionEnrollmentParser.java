package com.scholarscore.etl.schoolbrains;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Parses a section enrollment CSV and returns a map of source system section ID
 * to a list of source system student IDs.
 *
 * Created by markroper on 4/14/16.
 */
public class SectionEnrollmentParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionEnrollmentParser.class);
    protected File input;
    protected CSVParser parser;
    public SectionEnrollmentParser(File file) {
        this.input = file;
    }

    private static final int LASID = 0;
    private static final int SASID = 1;
    private static final int STUDENT_ID = 2;
    private static final int START_DATE = 3;
    private static final int END_DATE = 4;
    private static final int ENROLLMENT_STATUS = 5;
    private static final int GRADE_ID = 6;
    private static final int SECTION_ID = 7;


    public Map<String, List<String>> parse() {
        if(null == input) {
            return null;
        }
        try {
            Map<String, List<String>> results = new HashMap<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for (CSVRecord rec : parser) {
                String sectionId = rec.get(SECTION_ID);
                if(!results.containsKey(sectionId)) {
                    results.put(sectionId, new ArrayList<>());
                }
                String studentSsid = rec.get(STUDENT_ID);
                if(null != studentSsid && !studentSsid.isEmpty()) {
                    results.get(sectionId).add(studentSsid);
                }
            }
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse CSV file. " + e.getMessage());
            return null;
        }
    }
}
