package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.user.Student;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 4/14/16.
 */
public class StudentParser extends BaseParser<Student> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentParser.class);
    public StudentParser(File f) {
        super(f);
    }

    @Override
    public List<Student> parse() {
        if(null == input) {
            return null;
        }
        try {
            List<Student> results = new ArrayList<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for (CSVRecord rec : parser) {

            }
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse MCAS file. " + e.getMessage());
            return null;
        }
    }
}
