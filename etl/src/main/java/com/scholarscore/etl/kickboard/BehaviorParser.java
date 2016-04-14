package com.scholarscore.etl.kickboard;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses a Kickboard behavior csv export file into KickboardBehavior POJOs and does so with a BufferedReader
 * to avoid blowing out the heap.
 *
 * Created by markroper on 4/1/16.
 */
public class BehaviorParser extends BaseParser<KickboardBehavior> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BehaviorParser.class);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    Integer remoteBehaviorIdIdx, remoteStudentIdIdx, behaviorDateIdx, behaviorNameIdx, staffFirstIdx, staffLastIdx,
            behaviorCategoryIdx, meritPointsIdx, staffIdx, incidentIdIdx, externalIdIdx, firstNameIdx, lastNameIdx;

    public BehaviorParser(InputStream iis) {
        super(iis);
    }

    @Override
    protected void parseHeaderRow(String header, int index) {
        if("behavior id".equals(header)) {
            remoteBehaviorIdIdx = index;
        } else if("first name".equals(header)) {
            firstNameIdx = index;
        } else if("last name".equals(header)) {
            lastNameIdx = index;
        } else if("staff last name".equals(header)) {
            staffLastIdx = index;
        } else if("staff first name".equals(header)) {
            staffFirstIdx = index;
        } else if("student id".equals(header)) {
            remoteStudentIdIdx = index;
        } else if("external id".equals(header)) {
            externalIdIdx = index;
        } else if("date".equals(header)) {
            behaviorDateIdx = index;
        } else if("behavior".equals(header)) {
            behaviorNameIdx = index;
        } else if("category".equals(header)) {
            behaviorCategoryIdx = index;
        } else if("merit points".equals(header)) {
            meritPointsIdx = index;
        } else if("staff id".equals(header)) {
            staffIdx = index;
        } else if("incident id".equals(header)) {
            incidentIdIdx = index;
        }
    }

    @Override
    protected String getCsvFileName() {
        return "behavior";
    }

    @Override
    protected int getRetries() {
        return 40;
    }

    @Override
    protected KickboardBehavior resolveFromRecord(CSVRecord record) {
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
            if (null != behaviorCategoryIdx && record.size() > behaviorCategoryIdx) {
                behavior.category = record.get(behaviorCategoryIdx);
            }
        } catch (NumberFormatException nfe) {
            LOGGER.debug("(General) Unable to parse a long from input: " + nfe.getMessage());
        }
        return behavior;
    }
}
