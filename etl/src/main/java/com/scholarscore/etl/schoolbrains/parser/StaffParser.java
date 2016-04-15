package com.scholarscore.etl.schoolbrains.parser;

import com.scholarscore.models.user.Staff;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 4/15/16.
 */
public class StaffParser extends MultiEntityCsvParser<Staff> {
    public StaffParser(File file) {
        super(file);
    }

    @Override
    public Staff parseRec(CSVRecord rec) {
        Staff s = new Staff();
        s.setIsTeacher(true);
        s.setSourceSystemId(rec.get(TeacherID));
        s.setName(rec.get(Teacher));
        return s;
    }

    public Staff parseRec2(CSVRecord rec) {
        String ssid = rec.get(SecondTeacherID);
        String name = rec.get(SecondTeacher);
        if(null == ssid || ssid.trim().isEmpty() || "0".equals(ssid.trim())) {
            return null;
        }
        Staff s = new Staff();
        s.setIsTeacher(true);
        s.setSourceSystemId(ssid);
        s.setName(name);
        return s;
    }

    @Override
    public Set<Staff> parse() {
        if(null == input) {
            return null;
        }
        try {
            Set<Staff> results = new HashSet<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for (CSVRecord rec : parser) {
                Staff item = parseRec(rec);
                if(null != item) {
                    results.add(item);
                }
                Staff item2 = parseRec2(rec);
                if(null != item2) {
                    results.add(item2);
                }
            }
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse CSV file. " + e.getMessage());
            return null;
        }
    }
}
