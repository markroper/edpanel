package com.scholarscore.etl.powerschool.sync.student.ellsped;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markroper on 2/25/16.
 */
public class SpedEllParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpedEllParser.class);
    private final static String ID = "ID";
    private final static String SPED_MARKER = "MA_SpecEd621";
    private final static String SPED_MARKER_MATCH = "MA_PrimDisabil";
    private final static String ELL_MARKER = "MA_EngProficiency";

    public Map<Long, MutablePair<String, String>> parse(InputStream iis) {
        Map<Long, MutablePair<String, String>> returnMap = new HashMap<>();
        try {
            BufferedInputStream is = new BufferedInputStream(iis);
            is.mark(0);
            Reader in = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String header[] = br.readLine().split(",");
            is.reset();
            CSVParser parser = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : parser.getRecords()) {
                MutablePair<String, String> spedEll = new MutablePair<>();
                for (int i = 0; i < record.size(); i++) {
                    String name = header[i];
                    if (name.equalsIgnoreCase(ID)) {
                        String id = record.get(i);
                        if (id.matches("[0-9]{1,}")) {
                            returnMap.put(Long.valueOf(id), spedEll);
                        } else {
                            break;
                        }
                    } else if (name.startsWith(SPED_MARKER) || name.startsWith(SPED_MARKER_MATCH)) {
                        spedEll.setLeft(record.get(i));
                    } else if (name.startsWith(ELL_MARKER)) {
                        spedEll.setRight(record.get(i));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse SPED/ELL CSV.", e);
        }
        return returnMap;
    }
}
