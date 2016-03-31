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
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the pattern for updating GPA entries into the EdPanel API from a powerschool extract GPA file
 *
 * Created by mattg on 11/24/15.
 */
public class GpaSync implements ISync<Gpa> {
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

    /**
     * There's not enough details to determine how to perform add update, delete - unless the timestamp info is relevant to 'now' and only now, in which
     * case this method is always a create and nothing else?
     *
     * @param results A SynchResult instance to update as the sync proceeds
     * @return
     */
    @Override
    public ConcurrentHashMap<Long, Gpa> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Gpa> sourceValues = null;
        try {
            sourceValues = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            LOGGER.warn("Unable to resolve source system GPAs for students");
        }
        ConcurrentHashMap<Long, Gpa> edPanelValues = null;
        try {
            edPanelValues = resolveFromEdPanel();
        } catch (HttpClientException e) {
            LOGGER.error("failed to resolve GPAs from EdPanel", e);
            return null;
        }

        Iterator<Map.Entry<Long, Gpa>> sourceIterator = sourceValues.entrySet().iterator();
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Gpa> sourceEntry = sourceIterator.next();
            Gpa sourceGpa = sourceEntry.getValue();
            Gpa edPanelGpa = edPanelValues.get(sourceGpa.getStudentId());
            if(null == edPanelGpa || !edPanelGpa.getCalculationDate().equals(sourceGpa.getCalculationDate())) {
                try {
                    Gpa responseGpa = edPanel.createGpa(sourceGpa.getStudentId(), sourceGpa);
                    sourceGpa.setId(responseGpa.getId());
                } catch (HttpClientException e) {
                    LOGGER.error("Failed to create GPA in EdPanel." + sourceGpa.toString());
                }
            } else {
                try {
                    sourceGpa.setId(edPanelGpa.getId());
                    if(!edPanelGpa.equals(sourceGpa)) {
                        edPanel.updateGpa(sourceGpa.getStudentId(), sourceGpa);
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to update GPA in EdPanel." + sourceGpa.toString());
                }
            }
        }
        return sourceValues;
    }

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

    protected ConcurrentHashMap<Long, Gpa> resolveFromEdPanel() throws HttpClientException {
        Gpa[] gpas = edPanel.getGpas();
        ConcurrentHashMap<Long, Gpa> gpaMap = new ConcurrentHashMap<>();
        for(Gpa gpa: gpas) {
            gpaMap.put(gpa.getStudentId(), gpa);
        }
        return gpaMap;
    }
}
