package com.scholarscore.etl.powerschool.sync.student.gpa;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the generated CSV file from the CasperJS script, assumes a particular naming convention to determine the
 * header values and the associated types.  The expected header format is as follows:
 *
 * ID,*gpa_method="ADDED_VALUE",*gpa_method="simple",*gpa_method="SIMPLE_PERCENT",*gpa_method="ADDED_VALUE"_term="Q1",*gpa_method="simple"_term="Q1",*gpa_method="SIMPLE_PERCENT"_term="Q1",*gpa_method="ADDED_VALUE"_term="Q2",*gpa_method="simple"_term="Q2",*gpa_method="SIMPLE_PERCENT"_term="Q2",*gpa_method="ADDED_VALUE"_term="Q3",*gpa_method="simple"_term="Q3",*gpa_method="SIMPLE_PERCENT"_term="Q3",*gpa_method="ADDED_VALUE"_term="Q4",*gpa_method="simple"_term="Q4",*gpa_method="SIMPLE_PERCENT"_term="Q4"
 *
 * Created by mattg on 11/24/15.
 */
public class GpaParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(GpaParser.class);
    private final static String ID = "ID";
    private final static Pattern GPA_METHOD_MATCHER = Pattern.compile(".*gpa_method=\"([^\"]+)\"(_term=\"([^\"]+)\")?");
    private final static String GPA_METHOD_MARKER = "*gpa_method";

    public List<RawGpaValue> parse(InputStream iis) {
        List<RawGpaValue> results = new ArrayList<>();
        try {
            BufferedInputStream is = new BufferedInputStream(iis);
            is.mark(0);
            Reader in = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String header[] = br.readLine().split(",");
            is.reset();
            CSVParser parser = CSVFormat.DEFAULT.parse(in);

            for (CSVRecord record : parser.getRecords()) {
                RawGpaValue gpa = new RawGpaValue();
                for (int i = 0; i < record.size(); i++) {
                    String name = header[i];
                    if (name.equalsIgnoreCase(ID)) {
                        String id = record.get(i);
                        if (id.matches("[0-9]{1,}")) {
                            gpa.setStudentId(Long.valueOf(id));
                        }
                        else {
                            break;
                        }
                    } else if (name.startsWith(GPA_METHOD_MARKER)) {
                        Matcher m = GPA_METHOD_MATCHER.matcher(name);
                        if (m.matches()) {
                            if (m.groupCount() >= 1) {
                                String type = m.group(1);
                                gpa.setType(GpaType.fromString(type));
                                gpa.setValue(convertToDouble(record.get(i)));
                            }
                            if (m.groupCount() >= 2) {
                                String term = m.group(3);
                                Double value = convertToDouble(record.get(i));
                                gpa.getTermValues().put(term, value);
                            }
                        }
                    }
                }
                if (gpa.getStudentId() != null) {
                    results.add(gpa);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Unable to parse GPA CSV.", e);
        }
        return results;
    }

    /**
     * Convert string to double, if not double return null
     *
     * @param value
     * @return
     */
    private Double convertToDouble(String value) {
        if ((!(null == value || value.isEmpty()) && value.matches("[\\d\\.]{1,}"))) {
            return Double.valueOf(value);
        }
        return null;
    }
}
