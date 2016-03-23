package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentFactory;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIdWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentTypeWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.MissingStudentMigrator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by markroper on 10/28/15.
 */
public class SectionAssignmentSync implements ISync<Assignment> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionAssignmentSync.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private StudentAssociator studentAssociator;
    private Section createdSection;
    //Lookup mappings populated as a part of powerschool assignment resolution
    Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
    Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();

    public SectionAssignmentSync(IPowerSchoolClient powerSchool,
                                   IAPIClient edPanel,
                                   School school,
                                   StudentAssociator studentAssociator,
                                   Section createdSection) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.studentAssociator = studentAssociator;
        this.createdSection = createdSection;
    }

    @Override
    public ConcurrentHashMap<Long, Assignment> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Assignment> source = null;
        try {
            source = this.resolveAllFromSourceSystem(results);
        } catch (HttpClientException e) {
            LOGGER.warn("Unable to resolve section assignments for section with name: " +
                    createdSection.getName() +
                    ", ID: " + createdSection.getId() +
                    ", SSID: " + createdSection.getSourceSystemId() +
                    ", & School ID: " + school.getId());
            results.sectionAssignmentSourceGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(createdSection.getSourceSystemId()),
                    createdSection.getId());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, Assignment> ed = null;
        try {
            ed = this.resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.sectionAssignmentEdPanelGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(createdSection.getSourceSystemId()),
                    createdSection.getId());
            return new ConcurrentHashMap<>();
        }
        Iterator<Map.Entry<Long, Assignment>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        ExecutorService executor = Executors.newFixedThreadPool(EtlEngine.THREAD_POOL_SIZE);
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Assignment> entry = sourceIterator.next();
            Assignment sourceAssignment = entry.getValue();
            Assignment edPanelAssignment = ed.get(entry.getKey());
            if(null == edPanelAssignment){
                Assignment created = null;
                try {
                    created = edPanel.createSectionAssignment(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            sourceAssignment);
                } catch (HttpClientException e) {
                    results.sectionAssignmentCreateFailed(
                            Long.valueOf(createdSection.getSourceSystemId()),
                            Long.valueOf(sourceAssignment.getSourceSystemId()));
                    continue;
                }
                sourceAssignment.setId(created.getId());
                results.sectionAssignmentCreated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceAssignment.getId());
            } else {
                //Massage discrepencies to determine
                sourceAssignment.setId(edPanelAssignment.getId());
                if(sourceAssignment.getSectionFK().equals(edPanelAssignment.getSectionFK())) {
                    edPanelAssignment.setSection(sourceAssignment.getSection());
                }
                if(!edPanelAssignment.equals(sourceAssignment)) {
                    Assignment replaced = null;
                    try {
                        replaced = edPanel.replaceSectionAssignment(
                                school.getId(),
                                createdSection.getTerm().getSchoolYear().getId(),
                                createdSection.getTerm().getId(),
                                createdSection.getId(),
                                sourceAssignment);
                    } catch (IOException e) {
                        results.sectionAssignmentCreateFailed(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey());
                        continue;
                    }
                    results.sectionAssignmentUpdated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceAssignment.getId());
                }
            }
            //Regardless of wheter or not we're creating, updating or no-op-ing, sync the StudentAssignments
            StudentAssignmentSyncRunnable runnable = new StudentAssignmentSyncRunnable(
                        powerSchool,
                        edPanel,
                        school,
                        createdSection,
                        sourceAssignment,
                        ssidToStudent,
                        results
            );
            executor.execute(runnable);
        }
        executor.shutdown();
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Assignment>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Assignment> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteSectionAssignment(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            entry.getValue());
                } catch (HttpClientException e) {
                    results.sectionAssignmentDeleteFailed(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), entry.getValue().getId());
                    continue;
                }
                results.sectionAssignmentDeleted(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), entry.getValue().getId());
            }
        }
        //Spin while we wait for all the threads to complete
        try {
            if (!executor.awaitTermination(EtlEngine.TOTAL_TTL_MINUTES, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch(InterruptedException e) {
            LOGGER.error("Executor thread pool interrupted " + e.getMessage());
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Assignment> resolveAllFromSourceSystem(PowerSchoolSyncResult results) throws HttpClientException {
        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
        PsResponse<PsAssignmentTypeWrapper> powerTypes =
                powerSchool.getAssignmentCategoriesBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        if(null != powerTypes && null != powerTypes.record) {
            for (PsResponseInner<PsAssignmentTypeWrapper> pat: powerTypes.record) {
                if(null != pat.tables && null != pat.tables.pgcategories) {
                    typeIdToType.put(
                            Long.valueOf(pat.tables.pgcategories.getId()),
                            pat.tables.pgcategories);
                }
            }
        }
        //Now iterate over all the assignments and construct the correct type of EdPanel assignment
        PsResponse<PsAssignmentWrapper> powerAssignments =
                powerSchool.getAssignmentsBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        //Get the association between student section score ID and student ID
        PsResponse<PsSectionScoreIdWrapper> ssids = powerSchool.getStudentScoreIdsBySectionId(
                Long.valueOf(createdSection.getSourceSystemId()));
        if(null != ssids && null != ssids.record) {
            for(PsResponseInner<PsSectionScoreIdWrapper> ssid: ssids.record) {
                PsSectionScoreId i = ssid.tables.sectionscoresid;
                Long ssidId = Long.valueOf(i.getDcid());
                Student stud = studentAssociator.findBySourceSystemId(Long.valueOf(i.getStudentid()));
                if(null == stud) {
                    stud = MissingStudentMigrator.resolveMissingStudent(
                            school.getId(),
                            Long.valueOf(i.getStudentid()),
                            powerSchool,
                            edPanel,
                            studentAssociator,
                            results);
                } else {
                    LOGGER.warn("!! !! Student Associator findBySourceSystemId WORKS for student with ssid " + i.getStudentid());
                }
                
                if(null != stud) {
                    ssidToStudent.put(ssidId, new MutablePair<>(stud, i));
                } else {
                    LOGGER.warn("Unable to resolve student with ssid: " + i.getStudentid());
                }
            }
        }

        //THREADING BU SECTION ASSIGNMENT -> STUDENT ASSIGNMENT
        ConcurrentHashMap<Long, Assignment> source = new ConcurrentHashMap<>();
        if(null != powerAssignments && null != powerAssignments.record) {
            for (PsResponseInner<PsAssignmentWrapper> powerAssignment : powerAssignments.record) {
                PsAssignment pa = powerAssignment.tables.pgassignments;
                PsAssignmentType psType = typeIdToType.get(Long.valueOf(pa.getPgcategoriesid()));
                Assignment edpanelAssignment = PsAssignmentFactory.fabricate(
                        pa,
                        psType);
                edpanelAssignment.setWeight(pa.getWeight());
                edpanelAssignment.setSection(createdSection);
                edpanelAssignment.setUserDefinedType(psType.getName());
                edpanelAssignment.setIncludeInFinalGrades("1".equals(pa.getIncludeinfinalgrades()));
                edpanelAssignment.setSectionFK(createdSection.getId());
                edpanelAssignment.setSourceSystemId(pa.getDcid().toString());
                source.put(pa.getDcid(), edpanelAssignment);
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Assignment> resolveFromEdPanel() throws HttpClientException {
        ConcurrentHashMap<Long, Assignment> edpanelAssignments = new ConcurrentHashMap<>();
        Assignment[] edPanelAssignmentMap = edPanel.getSectionAssignments(
                school.getId(),
                createdSection.getTerm().getSchoolYear().getId(),
                createdSection.getTerm().getId(),
                createdSection.getId());
        for(Assignment ssg : edPanelAssignmentMap) {
            edpanelAssignments.put(
                    Long.valueOf(ssg.getSourceSystemId()),
                    ssg
            );
        }
        return edpanelAssignments;
    }
}
