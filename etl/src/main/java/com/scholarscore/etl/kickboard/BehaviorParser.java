package com.scholarscore.etl.kickboard;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 4/1/16.
 */
public class BehaviorParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(BehaviorParser.class);
    private InputStream iis;
    private BufferedReader br;
    private String[] headerRow;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    Integer remoteBehaviorIdIdx, remoteStudentIdIdx, behaviorDateIdx, behaviorNameIdx, staffFirstIdx, staffLastIdx,
            behaviorCategoryIdx, meritPointsIdx, staffIdx, incidentIdIdx, externalIdIdx, firstNameIdx, lastNameIdx;

    public BehaviorParser(InputStream iis) {
        this.iis = iis;
        initialize();
    }

    private void initialize() {
        BufferedInputStream is = new BufferedInputStream(iis);
        is.mark(0);
        Reader in = new InputStreamReader(is);
        br = new BufferedReader(new InputStreamReader(is));
        try {
            headerRow = br.readLine().split("\",\"");
            for(int i = 0; i < headerRow.length; i++) {
                String header = headerRow[i];
                if("behavior id".equals(header)) {
                    remoteBehaviorIdIdx = i;
                } else if("first name".equals(header)) {
                    firstNameIdx = i;
                } else if("last name".equals(header)) {
                    lastNameIdx = i;
                } else if("staff last name".equals(header)) {
                    staffLastIdx = i;
                } else if("staff first name".equals(header)) {
                    staffFirstIdx = i;
                } else if("\"student id".equals(header)) {
                    remoteStudentIdIdx = i;
                } else if("external id".equals(header)) {
                    externalIdIdx = i;
                } else if("date".equals(header)) {
                    behaviorDateIdx = i;
                } else if("behavior".equals(header)) {
                    behaviorNameIdx = i;
                } else if("category".equals(header)) {
                    behaviorCategoryIdx = i;
                } else if("merit points".equals(header)) {
                    meritPointsIdx = i;
                } else if("staff id".equals(header)) {
                    staffIdx = i;
                } else if("incident id\"".equals(header)) {
                    incidentIdIdx = i;
                }
            }
            is.reset();
        } catch (IOException e) {
            LOGGER.error("Unable to parse the header row of the behavior CSV file.");
        }
    }

    public void close() {
        if(null != this.iis) {
            try {
                this.iis.close();
            } catch (IOException e) {
                LOGGER.warn("Unable to close the input stream within the KickBoard CSV parser.");
            }
        }
        if(null != this.br) {
            try {
                this.br.close();
            } catch (IOException e) {
                LOGGER.warn("Unable to close the buffered reader within the KickBoard CSV parser.");
            }
        }
    }
    public List<KickboardBehavior> next(Integer chunkSize) {
        List<KickboardBehavior> results = new ArrayList<>();
        try {
            String currentLine = br.readLine();
            if(null == currentLine) {
                return null;
            }
            int count = 0;
            if(null == chunkSize || chunkSize < 0) {
                chunkSize = Integer.MAX_VALUE;
            }
            while(null != currentLine && count < chunkSize) {
                CSVParser p = CSVParser.parse(currentLine, CSVFormat.DEFAULT);
                List<CSVRecord> records = p.getRecords();
                if(null != records && records.size() > 0) {
                    CSVRecord record = records.get(0);
                    KickboardBehavior behavior = new KickboardBehavior();
                    try {
                        behavior.behaviorId = resolveLongValue(record, remoteBehaviorIdIdx);
                        behavior.studentId = resolveLongValue(record, remoteStudentIdIdx);
                        behavior.externalId = resolveLongValue(record, externalIdIdx);
                        behavior.staffId = resolveLongValue(record, staffIdx);
                        behavior.meritPoints = resolveLongValue(record, meritPointsIdx);
                        behavior.incidentId = resolveLongValue(record, incidentIdIdx);
                        if (null != behaviorDateIdx && record.size() > behaviorDateIdx) {
                            try {
                                behavior.date = LocalDate.parse(record.get(behaviorDateIdx), dtf);
                            } catch (DateTimeParseException e) {
                                LOGGER.debug("Unable to parse date from the behavior event: " + e.getMessage());
                            }
                        }
                        if (null != behaviorNameIdx && record.size() > behaviorNameIdx) {
                            behavior.behavior = record.get(behaviorNameIdx);
                        }
                        if (null != firstNameIdx && record.size() > firstNameIdx) {
                            behavior.firstName = record.get(firstNameIdx);
                        }
                        if (null != lastNameIdx && record.size() > lastNameIdx) {
                            behavior.lastName = record.get(lastNameIdx);
                        }
                        if (null != staffFirstIdx && record.size() > staffFirstIdx) {
                            behavior.staffFirstName = record.get(staffFirstIdx);
                        }
                        if (null != staffLastIdx && record.size() > staffLastIdx) {
                            behavior.staffLastName = record.get(staffLastIdx);
                        }
                        if(null != behaviorCategoryIdx && record.size() > behaviorCategoryIdx) {
                            behavior.category = record.get(behaviorCategoryIdx);
                        }
                    } catch (NumberFormatException nfe) {
                        LOGGER.debug("Unable to parse a long from input: " + nfe.getMessage());
                    }
                    results.add(behavior);
                }
                currentLine = br.readLine();
                count++;
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse behavior CSV row: " + e.getMessage());
        }
        return results;
    }

    private static Long resolveLongValue(CSVRecord record, Integer index) {
        try {
            if (null != index && record.size() > index) {
                return Long.parseLong(record.get(index));
            }
        } catch(NumberFormatException nfe) {
            LOGGER.debug("Unable to parse a long from input: " + nfe.getMessage());
        }
        return null;
    }

}
