package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
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
import com.scholarscore.etl.powerschool.sync.MultiThreadedSyncBase;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/28/15.
 * 
 * Section Assignment sync class
 */
public class SectionAssignmentSync extends MultiThreadedSyncBase<Assignment> implements ISync<Assignment> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionAssignmentSync.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private StudentAssociator studentAssociator;
    private Section createdSection;
    //Lookup mappings populated as a part of powerschool assignment resolution
    Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
    Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();
    // When calculating section grades, we get back an assignment's table ID from powerschool
    // and we need to convert this ID into one that identifies the saved assignment in EdPanel (SSID).
    private Map<Long, Long> assignmentTableIdToAssignmentSsid = new HashMap<>();
    
    public SectionAssignmentSync(IPowerSchoolClient powerSchool,
                                   IAPIClient edPanel,
                                   School school,
                                   StudentAssociator studentAssociator,
                                 Map<Long,Long> sectionPublicIdToSectionRecordId,
                                   Section createdSection) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.studentAssociator = studentAssociator;
        this.sectionPublicIdToSectionRecordId = sectionPublicIdToSectionRecordId;
        this.createdSection = createdSection;
    }

    private Map<Long,Long> sectionPublicIdToSectionRecordId = new HashMap<>();
    
    @Override
    protected Runnable buildRunnable(Assignment sourceRecord, PowerSchoolSyncResult results) {
                     //Regardless of whether or not we're creating, updating or no-op-ing, sync the StudentAssignments
        return new StudentAssignmentSyncRunnable(
                powerSchool,
                edPanel,
                school,
                createdSection,
                sourceRecord,
                ssidToStudent,
                assignmentTableIdToAssignmentSsid,
                results
        );
    }

    @Override
    protected ConcurrentHashMap<Long, Assignment> resolveAllFromSourceSystem() throws HttpClientException {
        
        // Some endpoints we are about to call require us to identify the section using the section's Database ID 
        // (aka record Id, table Id, or just 'id' if calling directly to a /table endpoint) instead of its SSID
        // so let's figure out all of the various ways to identify this section now
        Long sectionPublicId = Long.parseLong(createdSection.getSourceSystemId());
        Long sectionTableId = sectionPublicIdToSectionRecordId.get(sectionPublicId);
        LOGGER.trace("For Assignment w/ SSID " + createdSection.getSourceSystemId() + ", got tableId " + sectionTableId);

        // resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
        PsResponse<PsAssignmentTypeWrapper> powerTypes =
                powerSchool.getAssignmentCategoriesBySectionId(sectionTableId);
        
        if(null != powerTypes && null != powerTypes.record) {
            for (PsResponseInner<PsAssignmentTypeWrapper> pat: powerTypes.record) {
                if(null != pat.tables && null != pat.tables.pgcategories) {
                    typeIdToType.put(
                            Long.valueOf(pat.tables.pgcategories.getId()),
                            pat.tables.pgcategories);
                }
            }
        } else {
            LOGGER.info("PowerTypes response is null!");
        }
        
        // Now iterate over all the assignments and construct the correct type of EdPanel assignment.
        // The .getAssignmentsBySectionId call hits a 'table' endpoint, meaning instead of the section's SSID
        // we must supply its table ID.
        PsResponse<PsAssignmentWrapper> powerAssignments =
                powerSchool.getAssignmentsBySectionId(sectionTableId);

        // Also get the studentScoreIds (another table endpoint so we must use the tableId, not the SSID)
        PsResponse<PsSectionScoreIdWrapper> ssids = powerSchool.getStudentScoreIdsBySectionId(
                sectionTableId);

        if(null != ssids && null != ssids.record) {
            for(PsResponseInner<PsSectionScoreIdWrapper> sectionScoreIdWrapper: ssids.record) {
                PsSectionScoreId sectionScoreId = sectionScoreIdWrapper.tables.sectionscoresid;
                // We want to know what student this SectionScoreId is talking about, but we only have 'studentid' (tableID)
                Long entityTableId = Long.valueOf(sectionScoreId.getStudentid());
                // convert student's tableID into their SSID
                Long studentSsid = studentAssociator.findSsidFromTableId(entityTableId);
                if (null != studentSsid) {
                    Student stud = studentAssociator.findByUserSourceSystemId(studentSsid);
                    if (null != stud) {
                        // Key is PsSectionScoreId SSID/DCID (which still is specific to one kid), not student SSID
                        // This is used later, as SectionScoreAssignments will reference this SectionScoreId SSID
                        // (the value of FDCID in a SectionScoreAssignment correlates to the SSID of the PsSectionScoreId)
                        Long sectionScoreIdLong = Long.valueOf(sectionScoreId.getDcid());
                        ssidToStudent.put(sectionScoreIdLong, new MutablePair<>(stud, sectionScoreId));
                    } else {
                        // Here we have a section score with a 'studentid', which is the tableId of a student (not the SSID).
                        // We look up their SSID and find it, but we didn't pull that student from any of the schools. 
                        // In every case we've investigated so far, this is because these students have withdrawn.
                        
                        // TODO: Tracking and population of withdrawn students
                        // ... whereas the students we've seen so far are always associated with a school, using this DCID
                        // we can go directly to the student endpoint and get their information, even though they have withdrawn.
                        // This will allow us to capture their name, etc, as well as their withdrawn status in the DB. 
                        // This means we will be able to capture/correlate information on withdrawn students (probably more usefully, their historical grades)
                        // - As a first step, just record the names of Withdrawn students and print them at the end of the ETL?
                        LOGGER.warn("Student Presumed withdrawn -- found SSID " + studentSsid + " from (table)ID " + entityTableId + " but no schools returned this student");
                    }
                } else {
                    LOGGER.warn("Can't resolve (DC)ID from student table. Definitely cannot resolve" + " student with tableId " + entityTableId + ".");
                }
            }
        } else {
            LOGGER.info("getStudentScoreIdsBySectionId returned null or no records for createdSection w/ SSID " + createdSection.getSourceSystemId() + "!");
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
                assignmentTableIdToAssignmentSsid.put(pa.getDcid(), pa.getId());
            }
        } else {
            LOGGER.warn("PowerAssignments or .record is null!");
        }
        return source;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.warn("Unable to resolve section assignments for section with name: " +
                createdSection.getName() +
                ", ID: " + createdSection.getId() +
                ", SSID: " + createdSection.getSourceSystemId() +
                ", & School ID: " + school.getId());
        results.sectionAssignmentSourceGetFailed(
                Long.valueOf(createdSection.getSourceSystemId()),
                Long.valueOf(createdSection.getSourceSystemId()),
                createdSection.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Assignment> resolveFromEdPanel() throws HttpClientException {
        ConcurrentHashMap<Long, Assignment> edpanelAssignments = new ConcurrentHashMap<>();
        Collection<Assignment> edPanelAssignmentMap = edPanel.getSectionAssignments(
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

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        results.sectionAssignmentEdPanelGetFailed(
                Long.valueOf(createdSection.getSourceSystemId()),
                Long.valueOf(createdSection.getSourceSystemId()),
                createdSection.getId());
    }

    @Override
    protected void createEdPanelRecord(Assignment entityToSave, PowerSchoolSyncResult results) {
        Long sectionSsid = Long.valueOf(createdSection.getSourceSystemId());
        Long ssid = Long.valueOf(entityToSave.getSourceSystemId());
        try {
            Assignment created = edPanel.createSectionAssignment(
                    school.getId(),
                    createdSection.getTerm().getSchoolYear().getId(),
                    createdSection.getTerm().getId(),
                    createdSection.getId(),
                    entityToSave);
            entityToSave.setId(created.getId());
            results.sectionAssignmentCreated(sectionSsid, ssid, entityToSave.getId());
        } catch (HttpClientException e) {
            results.sectionAssignmentCreateFailed(
                    sectionSsid,
                    ssid);
        }
    }

    @Override
    protected void updateEdPanelRecord(Assignment sourceSystemEntity, Assignment edPanelEntity, PowerSchoolSyncResult results) {
        //Massage discrepancies to determine
        sourceSystemEntity.setId(edPanelEntity.getId());
        Long ssid = Long.valueOf(sourceSystemEntity.getSourceSystemId());
        Long sectionSsid = Long.valueOf(createdSection.getSourceSystemId());
        if (sourceSystemEntity.getSectionFK().equals(edPanelEntity.getSectionFK())) {
            edPanelEntity.setSection(sourceSystemEntity.getSection());
        }
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                edPanel.replaceSectionAssignment(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        sourceSystemEntity);
                results.sectionAssignmentUpdated(sectionSsid, ssid, sourceSystemEntity.getId());
            } catch (HttpClientException e) {
                results.sectionAssignmentCreateFailed(sectionSsid, ssid);
            }
        } else {
            results.sectionAssignmentUntouched(sectionSsid, ssid, sourceSystemEntity.getId());
        }
    }

    @Override
    protected void deleteEdPanelRecord(Assignment entityToDelete, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToDelete.getSourceSystemId());
        try {
            edPanel.deleteSectionAssignment(
                    school.getId(),
                    createdSection.getTerm().getSchoolYear().getId(),
                    createdSection.getTerm().getId(),
                    createdSection.getId(),
                    entityToDelete);
            results.sectionAssignmentDeleted(Long.valueOf(createdSection.getSourceSystemId()), ssid, entityToDelete.getId());
        } catch (HttpClientException e) {
            results.sectionAssignmentDeleteFailed(Long.valueOf(createdSection.getSourceSystemId()), ssid, entityToDelete.getId());
        }
    }
}
