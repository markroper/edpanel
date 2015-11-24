package com.scholarscore.etl.powerschool.sync.student;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by mattg on 11/24/15.
 */
public class GPAParser {
    private final static String ID = "ID";
    private final static Pattern GPA_METHOD_MATCHER = Pattern.compile(".*gpa_method=\"([^\"]+)\"(_term=\"([^\"]+)\")?");
    private final static String GPA_METHOD_MARKER = "*gpa_method";

    enum GPAType {
        simple,
        simple_percent,
        added_value;

        public static GPAType fromString(String value) {
            for (GPAType type : values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    class RawGPAValue {
        private GPAType type;
        private Double gpaValue;
        private String id;
        private HashMap<String, Double> termValues = new HashMap<>();
        private Double value;

        public void setType(GPAType type) {
            this.type = type;
        }

        public GPAType getType() {
            return type;
        }

        public Double getGpaValue() {
            return gpaValue;
        }

        public void setGpaValue(Double gpaValue) {
            this.gpaValue = gpaValue;
        }

        public HashMap<String, Double> getTermValues() {
            return termValues;
        }

        public void setTermValues(HashMap<String, Double> termValues) {
            this.termValues = termValues;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public List<RawGPAValue> parse(InputStream is) {
        List<RawGPAValue> results = new ArrayList<>();
        try {
            Reader in = new InputStreamReader(is);

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String header[] = br.readLine().split(",");

            CSVParser parser = CSVFormat.DEFAULT.parse(in);

            for (CSVRecord record : parser.getRecords()) {
                RawGPAValue gpa = new RawGPAValue();
                for (int i = 0; i < record.size(); i++) {
                    String name = header[i];
                    if (name.equalsIgnoreCase(ID)) {
                        String id = record.get(i);
                        gpa.setId(id);
                    } else if (name.startsWith(GPA_METHOD_MARKER)) {
                        Matcher m = GPA_METHOD_MATCHER.matcher(name);
                        if (m.matches()) {
                            if (m.groupCount() >= 1) {
                                String type = m.group(1);
                                gpa.setType(GPAType.fromString(type));
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
                results.add(gpa);
            }

        } catch (IOException e) {
            e.printStackTrace();
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
