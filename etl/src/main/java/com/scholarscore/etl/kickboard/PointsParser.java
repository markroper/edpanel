package com.scholarscore.etl.kickboard;

import com.scholarscore.models.behavior.BehaviorScore;
import com.scholarscore.models.user.Student;
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
public class PointsParser extends BaseParser<BehaviorScore> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PointsParser.class);
    private final DateTimeFormatter dashDtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    //"student id","external id","last name","first name","school name","group name","grade level","week of","weekly dollar points","weekly merit points","yearly dollar points","yearly merit points"
    Integer externalIdIdx, remoteStudentIdIdx, weeklyMeritPointsIdx, annualMeritPointsIdx, weekOfIdx;

    public PointsParser(InputStream iis) {
        super(iis);
    }

    @Override
    protected BehaviorScore resolveFromRecord(CSVRecord record) {
        BehaviorScore score = new BehaviorScore();
        try {
            score.setDate(LocalDate.parse(record.get(weekOfIdx), dashDtf));
        } catch (DateTimeParseException e) {
            LOGGER.debug("Unable to parse date from the behavior event: " + e.getMessage());
        }
        score.setCurrentAnnualScore(resolveLongValue(record, annualMeritPointsIdx));
        score.setCurrentWeeklyScore(resolveLongValue(record, weeklyMeritPointsIdx));
        Student st = new Student();
        st.setSourceSystemId(String.valueOf(resolveLongValue(record, remoteStudentIdIdx)));
        st.setSourceSystemUserId(String.valueOf(resolveLongValue(record, externalIdIdx)));
        score.setStudent(st);
        if(null == score.getDate()) {
            return null;
        }
        return score;
    }

    @Override
    protected void parseHeaderRow(String header, int index) {
        if("student id".equals(header)) {
            remoteStudentIdIdx = index;
        } else if("external id".equals(header)) {
            externalIdIdx = index;
        } else if("weekly merit points".equals(header)) {
            weeklyMeritPointsIdx = index;
        } else if("yearly merit points".equals(header)) {
            annualMeritPointsIdx = index;
        } else if("week of".equals(header)) {
            weekOfIdx = index;
        }
    }

    @Override
    protected String getCsvFileName() {
        return "score";
    }

    @Override
    protected int getRetries() {
        return 50;
    }

}
