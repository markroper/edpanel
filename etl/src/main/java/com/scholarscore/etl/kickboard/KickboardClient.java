package com.scholarscore.etl.kickboard;

import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.models.behavior.BehaviorScore;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Created by markroper on 4/1/16.
 */
public class KickboardClient extends BaseHttpClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(KickboardClient.class);
    private File behaviorCsv;
    private File pointsCsv;
    private File consequenceCsv;
    private BehaviorParser parser;
    private PointsParser pointsParser;
    private ConsequenceParser consParser;

    private final URI behaviorUri;
    private final URI bankUri;
    private final URI consequenceUri;

    // these need the client KEY appended to the end in order to be valid 
    // in order to keep it simple, just append it to the end (this won't work if we have params not at the end)
    private static final String BEHAVIOR_PATH = "export/download/file/behavior/key/"; // + CLIENT_ID
    private static final String BANK_PATH = "export/download/file/points/key/"; // + CLIENT_ID
    private static final String CONSEQUENCE_PATH = "export/download/file/conroster/key/"; // + CLIENT_ID
    
    public KickboardClient(URI baseUri, String key) {
        super(baseUri);
        String base = "";
        if (baseUri != null) { base = baseUri.toString(); }
        if (!base.endsWith("/")) { base = base + "/"; }
        this.behaviorUri = URI.create(base + BEHAVIOR_PATH + key);
        this.bankUri = URI.create(base + BANK_PATH + key);
        this.consequenceUri = URI.create(base + CONSEQUENCE_PATH + key);
    }

    private FileInputStream downloadFile(URI u, String fileName, File file) throws FileNotFoundException {
        try {
            InputStream input = u.toURL().openStream();
            file = File.createTempFile(fileName, ".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            IOUtils.copy(input, bw);
            input.close();
            bw.close();
        } catch (IOException e) {
            LOGGER.error("Failed to download behavior CSV from KickBoard, unable to open stream.");
        }
        return new FileInputStream(file);
    }

    public List<BehaviorScore> getBehaviorScore(Integer chunkSize) {
        try {
            if(null == pointsParser) {
                InputStream fileInput = downloadFile(bankUri, "kickboardPoints", pointsCsv);
                pointsParser = new PointsParser(fileInput);
            }
            return pointsParser.next(chunkSize);
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to read in the points CSV file.");
        }
        return null;
    }


    public List<KickboardBehavior> getBehaviorData(Integer chunkSize) {
        try {
            if(null == parser) {
                InputStream fileInput = downloadFile(behaviorUri, "kickboardBehavior", behaviorCsv);
                parser = new BehaviorParser(fileInput);
            }
            return parser.next(chunkSize);
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to read in the behavior CSV file.");
        }
        return null;
    }

    public List<KickboardBehavior> getConsequenceData(Integer chunkSize) {
        try {
            if(null == consParser) {
                InputStream fileInput = downloadFile(consequenceUri, "kickboardConsequence", consequenceCsv);
                consParser = new ConsequenceParser(fileInput);
            }
            return consParser.next(chunkSize);
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to read in the consequence CSV file.");
        }
        return null;
    }

    public void close() {
        if(null != parser) {
            parser.close();
        }
        if(null != pointsParser) {
            pointsParser.close();
        }
        if(null != consParser) {
            consParser.close();
        }
        if(null != behaviorCsv) {
            behaviorCsv.delete();
        }
        if(null != consequenceCsv) {
            consequenceCsv.delete();
        }
        if(null != pointsCsv) {
            pointsCsv.delete();
        }
    }
    @Override
    protected void authenticate() throws HttpClientException {
        //NO OP, only the API key is required
    }

    @Override
    protected Boolean isAuthenticated() {
        //NO OP, only the API key is required, provided in the URL
        return true;
    }
}
