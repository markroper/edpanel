package com.scholarscore.etl.powerschool.sync.student.gpa;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.gpa.Gpa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the pattern for updating GPA entries into the EdPanel API from a powerschool extract GPA file
 *
 * Created by mattg on 11/24/15.
 */
public class GPASync implements ISync<Gpa> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GPASync.class);

    private final LocalDate syncCutoff;
    private final StudentAssociator studentAssociator;
    private final IPowerSchoolClient powerSchool;
    private final IAPIClient edPanel;
    private final List<File> gpaFiles;


    public GPASync(List<File> gpaFiles,
                   IAPIClient edPanel,
                   IPowerSchoolClient powerSchool,
                   StudentAssociator studentAssociator,
                   LocalDate syncCutoff) {
        this.gpaFiles = gpaFiles;
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
    public ConcurrentHashMap<Long, Gpa> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        GPAParser parser = new GPAParser();
        ConcurrentHashMap<Long, Gpa> resultValues = new ConcurrentHashMap<>();
        try {
            for(File gpaFile : gpaFiles){
                if(gpaFile.canRead() && gpaFile.isFile()) {
                    List<RawGPAValue> gpas = parser.parse(new FileInputStream(gpaFile));
                    for (RawGPAValue value : gpas) {
                        Gpa gpa = value.emit();
                        gpa.setStudentId(studentAssociator.findBySourceSystemId(gpa.getStudentId()).getId());
                        // Create the GPA entry for the student by studentId
                        Gpa responseGpa = edPanel.createGPA(gpa.getStudentId(), gpa);
                        resultValues.put(gpa.getStudentId(), responseGpa);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create new GPA", e);
        }
        return resultValues;
    }
}
