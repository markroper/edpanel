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
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Integer remoteBehaviorIdIdx, remoteStudentIdIdx, behaviorDateIdx, behaviorNameIdx,
            behaviorCategoryIdx, meritPointsIdx, staffIdx, incidentIdIdx, externalIdIdx;

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
            headerRow = br.readLine().split(",");
            for(int i = 0; i < headerRow.length; i++) {
                String header = headerRow[i];
                if("behaviorId".equals(header)) {
                    remoteBehaviorIdIdx = i;
                } else if("studentId".equals(header)) {
                    remoteStudentIdIdx = i;
                } else if("externalId".equals(header)) {
                    externalIdIdx = i;
                } else if("date".equals(header)) {
                    behaviorDateIdx = i;
                } else if("behavior".equals(header)) {
                    behaviorNameIdx = i;
                } else if("category".equals(header)) {
                    behaviorCategoryIdx = i;
                } else if("meritPoints".equals(header)) {
                    meritPointsIdx = i;
                } else if("staffId".equals(header)) {
                    staffIdx = i;
                } else if("incidentId".equals(incidentIdIdx)) {
                    incidentIdIdx = i;
                }
            }
            is.reset();
        } catch (IOException e) {
            LOGGER.error("Unable to parse the header row of the behavior CSV file.");
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
                    if(null != remoteBehaviorIdIdx && record.size() > remoteBehaviorIdIdx) {
                        behavior.behaviorId = Long.parseLong(record.get(remoteBehaviorIdIdx));
                    }
                    if(null != remoteStudentIdIdx && record.size() > remoteStudentIdIdx) {
                        behavior.studentId = Long.parseLong(record.get(remoteStudentIdIdx));
                    }
                    if(null != externalIdIdx && record.size() > externalIdIdx) {
                        behavior.externalId = Long.parseLong(record.get(externalIdIdx));
                    }
                    if(null != behaviorDateIdx && record.size() > behaviorDateIdx) {
                        behavior.date = LocalDate.parse(record.get(behaviorDateIdx), dtf);
                    }
                    if(null != behaviorNameIdx && record.size() > behaviorNameIdx) {
                        behavior.behavior = record.get(behaviorNameIdx);
                    }
                    if(null != staffIdx && record.size() > staffIdx) {
                        behavior.staffId = Long.parseLong(record.get(staffIdx));
                    }
                    if(null != meritPointsIdx && record.size() > meritPointsIdx) {
                        behavior.meritPoints = Long.parseLong(record.get(meritPointsIdx));
                    }
                    if(null != incidentIdIdx && record.size() > incidentIdIdx) {
                        behavior.incidentId = Long.parseLong(record.get(incidentIdIdx));
                    }
                    results.add(behavior);
                }
                currentLine = br.readLine();
                count++;
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse GPA CSV.", e);
        }
        return results;
    }
}
