package com.scholarscore.etl.kickboard;

import com.scholarscore.models.behavior.BehaviorScore;
import com.scholarscore.models.user.Student;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a Kickboard behavior csv export file into KickboardBehavior POJOs and does so with a BufferedReader
 * to avoid blowing out the heap.
 *
 * Created by markroper on 4/1/16.
 */
public class PointsParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(PointsParser.class);
    private InputStream iis;
    private BufferedReader br;
    private String[] headerRow;
    private final DateTimeFormatter dashDtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    //"student id","external id","last name","first name","school name","group name","grade level","week of","weekly dollar points","weekly merit points","yearly dollar points","yearly merit points"
    Integer externalIdIdx, remoteStudentIdIdx, weeklyMeritPointsIdx, annualMeritPointsIdx, weekOfIdx;

    public PointsParser(InputStream iis) {
        this.iis = iis;
        initialize();
    }

    /**
     * Initializes the BufferedInputStream on the input stream provided.  Parses the header row from the
     * input stream provided so that CSV rows can be mapped to the KickboardBehavior POJOs within the next(..) method.
     */
    private void initialize() {
        BufferedInputStream is = new BufferedInputStream(iis);
        is.mark(0);
        br = new BufferedReader(new InputStreamReader(is));
        try {
            headerRow = br.readLine().split("\",\"");
            for(int i = 0; i < headerRow.length; i++) {
                String header = headerRow[i];
                if("\"student id".equals(header)) {
                    remoteStudentIdIdx = i;
                } else if("external id".equals(header)) {
                    externalIdIdx = i;
                } else if("weekly merit points".equals(header)) {
                    weeklyMeritPointsIdx = i;
                } else if("yearly merit points\"".equals(header)) {
                    annualMeritPointsIdx = i;
                } else if("week of".equals(header)) {
                    weekOfIdx = i;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse the header row of the behavior CSV file.");
        }
    }

    /**
     * Closes file reader and input streams, should be called at the end of use.
     */
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

    /**
     * Returns the next list of KickboardBehavior instances parsed from the next chunkSize number of lines
     * from the CSV file that the class maintains a bufferedreader on.
     *
     * @param chunkSize
     * @return
     */
    public List<BehaviorScore> next(Integer chunkSize) {
        List<BehaviorScore> results = new ArrayList<>();
        String currentLine = null;
        try {
            currentLine = br.readLine();
        } catch (IOException e) {
            LOGGER.warn("Unable to parse behavior CSV row: " + e.getMessage());
            return this.next(chunkSize);
        }
        if(null == currentLine) {
            return null;
        }
        int count = 0;
        if(null == chunkSize || chunkSize < 0) {
            chunkSize = Integer.MAX_VALUE;
        }
        while(null != currentLine && count < chunkSize) {
            BehaviorScore score;
            try {
                score = resolveScoreFromLine(currentLine, br, 50);
                if(null != score) {
                    results.add(score);
                }
                currentLine = br.readLine();
            } catch (IOException e) {
                LOGGER.warn("Unable to parse score CSV row: " + e.getMessage());
                LOGGER.warn("Current row: " + currentLine);
            }
            count++;
        }
        return results;
    }

    /**
     * Kickboard appears not to escape new line characters in its CSV output. This means BufferedReader.readLine()
     * can sometimes chop a CSV line depending on input.  This method handles those cases, and returns null
     * in cases where a row cannot be resolved.
     *
     * @param line
     * @param br
     * @param retries
     * @return
     */
    private BehaviorScore resolveScoreFromLine(String line, BufferedReader br, int retries) {
        for(int i = 0; i < retries; i++) {
            try {
                BehaviorScore b = resolveScoreFromLine(line);
                if(null != b) {
                    return b;
                }
            } catch(IOException e) {
                try {
                    line += br.readLine();
                } catch (IOException e1) {
                    LOGGER.info("Unable to read from file input stream " + e.getMessage());
                }
            }
        }
        LOGGER.warn("Unable to parse CSV row from input: " + line);
        return null;
    }

    /**
     * Given a string, attempt to parse a CSV row from that string and map it into a BehaviorScore POJO.
     * Throwing an IOException if the input cannot be parsed.
     *
     * @param line
     * @return
     * @throws IOException
     */
    private BehaviorScore resolveScoreFromLine(String line) throws IOException {
        CSVParser p = CSVParser.parse(line, CSVFormat.DEFAULT);
        List<CSVRecord> records = p.getRecords();
        if (null != records && records.size() > 0) {
            CSVRecord record = records.get(0);
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
        return null;
    }

    /**
     * Attempt to parse a long from a CSV record value at a given index.  Return null if
     * the value cannot be resolved.
     *
     * @param record
     * @param index
     * @return
     */
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
