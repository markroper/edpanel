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
import java.util.ArrayList;
import java.util.List;

/**
 * User: jordan
 * Date: 4/14/16
 * Time: 2:58 PM
 */
public abstract class BaseParser<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BehaviorParser.class);
    
    private InputStream is;
    private BufferedReader br;
    
    public BaseParser(InputStream is) {
            this.is = is;
            initialize();
    }
    
    /**
     * Initializes the BufferedInputStream on the input stream provided.  Parses the header row from the
     * input stream provided so that CSV rows can be mapped to the KickboardBehavior POJOs within the next(..) method.
     */
    private void initialize() {
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(0);
        br = new BufferedReader(new InputStreamReader(bis));
        try {
            String[] headerRow = br.readLine().split(",");
            for(int i = 0; i < headerRow.length; i++) {
                String header = headerRow[i];
                header = header.replaceAll("^\"|\"$", "");
                parseHeaderRow(header, i);
            }
            bis.reset();
        } catch (IOException e) {
            LOGGER.error("Unable to parse the header row of the " + getCsvFileName() + " CSV file.");
        }
    }

    /**
     * Closes file reader and input streams, should be called at the end of use.
     */
    public void close() {
        if(null != this.is) {
            try {
                this.is.close();
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
    public List<T> next(Integer chunkSize) {
        List<T> results = new ArrayList<>();
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
            T entity;
            try {
                entity = resolveFromLine(currentLine, br, getRetries());
                if(null != entity) {
                    results.add(entity);
                }
                currentLine = br.readLine();
            } catch (IOException e) {
                LOGGER.warn("Unable to parse " + getCsvFileName() + " CSV row: " + e.getMessage());
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
    protected T resolveFromLine(String line, BufferedReader br, int retries) {
        for(int i = 0; i < retries; i++) {
            try {
                T entity = resolveFromLine(line);
                if(null != entity) {
                    return entity;
                }
            } catch(IOException e) {
                try {
                    line += br.readLine();
                } catch (IOException e1) {
                    LOGGER.info("Unable to read from file input stream, got two exceptions (ignoring 2nd): " + e.getMessage());
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
    private T resolveFromLine(String line) throws IOException {
        CSVParser p = CSVParser.parse(line, CSVFormat.DEFAULT);
        List<CSVRecord> records = p.getRecords();
        if (null != records && records.size() > 0) {
            LOGGER.trace("Taking record 0 only, but other records also found.");
            CSVRecord record = records.get(0);
            return resolveFromRecord(record);
        }
        return null;
    }

    protected abstract T resolveFromRecord(CSVRecord record);

    /**
     * Attempt to parse a long from a CSV record value at a given index.  Return null if
     * the value cannot be resolved.
     *
     * @param record
     * @param index
     * @return
     */
    protected static Long resolveLongValue(CSVRecord record, Integer index) {
        if (null != index && record.size() > index) {
            String fetched = record.get(index);
            try {
                return Long.parseLong(fetched);
            } catch (NumberFormatException nfe) {
                LOGGER.debug("ResolveLongValue unable to parse a long from input: " + fetched);
            }
        }
        return null;
    }
    
    protected abstract void parseHeaderRow(String headerRow, int index);

    protected abstract String getCsvFileName();
    
    protected abstract int getRetries();
}
