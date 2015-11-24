package com.scholarscore.etl.powerschool.sync.student;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.gpa.Gpa;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mattg on 11/24/15.
 */
public class GPASync implements ISync<Gpa> {

    private final LocalDate syncCutoff;
    private final StudentAssociator studentAssociator;
    private final IPowerSchoolClient powerSchool;
    private final IAPIClient edPanel;
    private final File gpaFile;


    public GPASync(File gpaFile, IAPIClient edPanel, IPowerSchoolClient powerSchool, StudentAssociator studentAssociator, LocalDate syncCutoff) {
        this.gpaFile = gpaFile;
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.studentAssociator = studentAssociator;
        this.syncCutoff = syncCutoff;
    }

    @Override
    public ConcurrentHashMap<Long, Gpa> syncCreateUpdateDelete(SyncResult results) {

        return null;
    }
}
