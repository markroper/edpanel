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
    private BehaviorParser parser;
    private PointsParser pointsParser;
    private URI bankUri;

    public KickboardClient(URI behaviorUri, URI bankUri) {
        super(behaviorUri);
        this.bankUri = bankUri;
    }

    private void downloadFile() {
        try {
            InputStream input = uri.toURL().openStream();
            behaviorCsv = File.createTempFile("kickboardBehavior", ".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(behaviorCsv, false));
            IOUtils.copy(input, bw);
            input.close();
            bw.close();
        } catch (IOException e) {
            LOGGER.error("Failed to download behavior CSV from KickBoard, unable to open stream.");
        }
    }

    private void downloadScoreFile() {
        try {
            InputStream input = bankUri.toURL().openStream();
            pointsCsv = File.createTempFile("kickboardPoints", ".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(pointsCsv, false));
            IOUtils.copy(input, bw);
            input.close();
            bw.close();
        } catch (IOException e) {
            LOGGER.error("Failed to download behavior CSV from KickBoard, unable to open stream.");
        }
    }

    public List<BehaviorScore> getBehaviorScore(Integer chunkSize) {
        if(null == pointsCsv) {
            downloadScoreFile();
        }
        if(null == pointsCsv) {
            return null;
        }
        if(null == pointsParser) {
            try {
                InputStream fileInput = new FileInputStream(pointsCsv);
                pointsParser = new PointsParser(fileInput);
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to read in the points CSV file.");
                return null;
            }
        }
        return pointsParser.next(chunkSize);
    }


    public List<KickboardBehavior> getBehaviorData(Integer chunkSize) {
        if(null == behaviorCsv) {
            downloadFile();
        }
        if(null == behaviorCsv) {
            return null;
        }
        if(null == parser) {
            try {
                InputStream fileInput = new FileInputStream(behaviorCsv);
                parser = new BehaviorParser(fileInput);
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to read in the behavior CSV file.");
                return null;
            }
        }
        return parser.next(chunkSize);
    }

    public void close() {
        if(null != parser) {
            parser.close();
        }
        if(null != pointsParser) {
            pointsParser.close();
        }
        if(null != behaviorCsv) {
            behaviorCsv.delete();
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
