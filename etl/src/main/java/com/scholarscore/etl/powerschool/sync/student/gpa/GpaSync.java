package com.scholarscore.etl.powerschool.sync.student.gpa;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.student.PsRankAndGpa;
import com.scholarscore.etl.powerschool.api.model.student.PsRankAndGpaWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SyncBase;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the pattern for updating GPA entries into the EdPanel API from a powerschool extract GPA file
 *
 * Created by mattg on 11/24/15.
 */
public class GpaSync extends SyncBase<Gpa> implements ISync<Gpa> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GpaSync.class);

    private final LocalDate syncCutoff;
    private final StudentAssociator studentAssociator;
    private final IPowerSchoolClient powerSchool;
    private final IAPIClient edPanel;
    private final List<File> gpaFiles;


    public GpaSync(List<File> gpaFiles,
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
    
    @Override
    protected ConcurrentHashMap<Long, Gpa> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, Gpa> resultValues = new ConcurrentHashMap<>();
        if(null != gpaFiles && gpaFiles.size() > 0) {
            GpaParser parser = new GpaParser();
            try {
                for (File gpaFile : gpaFiles) {
                    if (gpaFile.canRead() && gpaFile.isFile()) {
                        List<RawGpaValue> gpas = parser.parse(new FileInputStream(gpaFile));
                        for (RawGpaValue value : gpas) {
                            Gpa gpa = value.emit();
                            Student s = studentAssociator.findByUserSourceSystemId(value.getStudentId());
                            if (null != s) {
                                gpa.setStudentId(s.getId());
                                resultValues.put(gpa.getStudentId(), gpa);
                            } else {
                                LOGGER.warn("Unable to resolve the student with source system ID of: " +
                                        gpa.getStudentId());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to resolve GPA from file", e);
            }
        } else {
            PsResponse<PsRankAndGpaWrapper> gpas = powerSchool.getStudentRankAndGpas();
            if(null != gpas) {
                for (PsResponseInner<PsRankAndGpaWrapper> gpa : gpas.record) {
                    PsRankAndGpa w = gpa.tables.classrank;
                    RawGpaValue val = new RawGpaValue();
                    val.setType(GpaType.fromString(w.gpamethod.toUpperCase()));
                    Gpa edG = val.emit();
                    Student s = studentAssociator.findByUserSourceSystemId(
                            studentAssociator.findSsidFromTableId(w.studentid));
                    if(null != edG && null != s) {
                        edG.setScore(w.gpa);
                        edG.setStudentId(s.getId());
                        resultValues.put(edG.getStudentId(), edG);
                    }
                }
            }
        }
        return resultValues;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.warn("Unable to resolve source system GPAs for students");
    }

    @Override
    protected ConcurrentHashMap<Long, Gpa> resolveFromEdPanel() throws HttpClientException {
        Collection<Gpa> gpas = edPanel.getGpas();
        ConcurrentHashMap<Long, Gpa> gpaMap = new ConcurrentHashMap<>();
        for(Gpa gpa: gpas) {
            gpaMap.put(gpa.getStudentId(), gpa);
        }
        return gpaMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("failed to resolve GPAs from EdPanel");
    }

    @Override
    protected void createEdPanelRecord(Gpa entityToSave, PowerSchoolSyncResult results) {
        try {
            Gpa responseGpa = edPanel.createGpa(entityToSave.getStudentId(), entityToSave);
            entityToSave.setId(responseGpa.getId());
        } catch (HttpClientException e) {
            LOGGER.error("Failed to create GPA in EdPanel." + entityToSave.toString());
        }
    }

    @Override
    protected void updateEdPanelRecord(Gpa sourceSystemEntity, Gpa edPanelEntity, PowerSchoolSyncResult results) {
        // record exists, but if the date wasn't today, create a new one
        if (!edPanelEntity.getCalculationDate().equals(sourceSystemEntity.getCalculationDate())) {
            try {
                Gpa responseGpa = edPanel.createGpa(sourceSystemEntity.getStudentId(), sourceSystemEntity);
                sourceSystemEntity.setId(responseGpa.getId());
            } catch (HttpClientException e) {
                LOGGER.error("Failed to create GPA in EdPanel." + sourceSystemEntity.toString());
            }
        } else {
            try {
                sourceSystemEntity.setId(edPanelEntity.getId());
                if (!edPanelEntity.equals(sourceSystemEntity)) {
                    edPanel.updateGpa(sourceSystemEntity.getStudentId(), sourceSystemEntity);
                }
            } catch (HttpClientException e) {
                // TODO: record success/failure/untouched on GPA
                LOGGER.error("Failed to update GPA in EdPanel." + sourceSystemEntity.toString());
            }
        }
    }

    @Override
    protected void deleteEdPanelRecord(Gpa entityToDelete, PowerSchoolSyncResult results) {
        // we seem to not ever delete these
    }
}
