package com.scholarscore.etl.powerschool.sync.student.gpa;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the pattern for updating GPA entries into the EdPanel API from a powerschool extract GPA file
 * 
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

    /**
     * There's not enough details to determine how to perform add update, delete - unless the timestamp info is relevant to 'now' and only now, in which
     * case this method is always a create and nothing else?
     *
     * @param results A SynchResult instance to update as the sync proceeds
     * @return
     */
    @Override
    public ConcurrentHashMap<Long, Gpa> syncCreateUpdateDelete(SyncResult results) {
        GPAParser parser = new GPAParser();
        ConcurrentHashMap<Long, Gpa> resultValues = new ConcurrentHashMap<>();
        try {
            List<RawGPAValue> gpas = parser.parse(new FileInputStream(gpaFile));
            for (RawGPAValue value : gpas) {
                Gpa gpa = value.emit();
                resultValues.put(gpa.getStudentId(), gpa);
            }
        } catch (FileNotFoundException e) {

        }
        return resultValues;
    }
}
