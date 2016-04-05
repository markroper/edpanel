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
 * Parses a Kickboard behavior csv export file into KickboardBehavior POJOs and does so with a BufferedReader
 * to avoid blowing out the heap.
 *
 * Created by markroper on 4/1/16.
 */
public class ConsequenceParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsequenceParser.class);
    private InputStream iis;
    private BufferedReader br;
    private String[] headerRow;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter dtfDash = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //"school name","student id","external id","last name","first name","grade","group id","group name","phone","consequence id","consequence type","consequence name","assigned date","reasons","hours assigned","staff id","staff name","school id","attendance","external school id"
    Integer remoteStudentIdIdx, behaviorDateIdx, behaviorNameIdx, staffFirstIdx, staffLastIdx,
            behaviorCategoryIdx, staffIdx, externalIdIdx, firstNameIdx, lastNameIdx;

    public ConsequenceParser(InputStream iis) {
        this.iis = iis;
        initialize();
    }

    /**
     * Initializes the BufferedInputStream on the input stream provided.  Parses the header row from the
     * input stream provided so that CSV rows can be mapped to the KickboardBehavior POJOs within the next(..) method.
     */
    private void initialize() {
        BufferedInputStream is = new BufferedInputStream(iis);
        Reader in = new InputStreamReader(is);
        br = new BufferedReader(new InputStreamReader(is));
        try {
            headerRow = br.readLine().split(",");
            for(int i = 0; i < headerRow.length; i++) {
                String header = headerRow[i];
                header = header.replaceAll("^\"|\"$", "");
                if("first name".equals(header)) {
                    firstNameIdx = i;
                } else if("last name".equals(header)) {
                    lastNameIdx = i;
                } else if("staff last name".equals(header)) {
                    staffLastIdx = i;
                } else if("staff first name".equals(header)) {
                    staffFirstIdx = i;
                } else if("student id".equals(header)) {
                    remoteStudentIdIdx = i;
                } else if("external id".equals(header)) {
                    externalIdIdx = i;
                } else if("assigned date".equals(header)) {
                    behaviorDateIdx = i;
                } else if("consequence name".equals(header)) {
                    behaviorCategoryIdx = i;
                } else if("consequence type".equals(header)) {
                    behaviorNameIdx = i;
                } else if("staff id".equals(header)) {
                    staffIdx = i;
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
    public List<KickboardBehavior> next(Integer chunkSize) {
        List<KickboardBehavior> results = new ArrayList<>();
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
            KickboardBehavior behavior;
            try {
                behavior = resolveBehaviorFromLine(currentLine, br, 20);
                if(null != behavior) {
                    results.add(behavior);
                }
                currentLine = br.readLine();
            } catch (IOException e) {
                LOGGER.warn("Unable to parse behavior CSV row: " + e.getMessage());
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
    private KickboardBehavior resolveBehaviorFromLine(String line, BufferedReader br, int retries) {
        for(int i = 0; i < retries; i++) {
            try {
                KickboardBehavior b = resolveBehaviorFromLine(line);
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
     * Given a string, attempt to parse a CSV row from that string and map it into a KickboardBehavior POJO.
     * Throwing an IOException if the input cannot be parsed.
     *
     * @param line
     * @return
     * @throws IOException
     */
    private KickboardBehavior resolveBehaviorFromLine(String line) throws IOException {
        CSVParser p = CSVParser.parse(line, CSVFormat.DEFAULT);
        List<CSVRecord> records = p.getRecords();
        if (null != records && records.size() > 0) {
            CSVRecord record = records.get(0);
            KickboardBehavior behavior = new KickboardBehavior();
            try {
                behavior.studentId = resolveLongValue(record, remoteStudentIdIdx);
                behavior.externalId = resolveLongValue(record, externalIdIdx);
                behavior.staffId = resolveLongValue(record, staffIdx);
                if (null != behaviorDateIdx && record.size() > behaviorDateIdx) {
                    try {
                        behavior.date = LocalDate.parse(record.get(behaviorDateIdx), dtf);
                    } catch (DateTimeParseException e) {
                        try {
                            behavior.date = LocalDate.parse(record.get(behaviorDateIdx), dtfDash);
                        } catch(DateTimeParseException ex) {
                            LOGGER.debug("Unable to parse date from the behavior event: " + ex.getMessage());
                        }
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
                LOGGER.debug("Unable to parse a long from input: " + nfe.getMessage());
            }
            return behavior;
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
