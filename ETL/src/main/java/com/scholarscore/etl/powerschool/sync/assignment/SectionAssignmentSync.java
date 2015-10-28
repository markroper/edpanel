package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentFactory;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIds;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentType;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by markroper on 10/28/15.
 */
public class SectionAssignmentSync implements ISync<Assignment> {
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private StudentAssociator studentAssociator;
    private List<Long> unresolvablePowerStudents;
    private Section createdSection;
    //Lookup mappings populated as a part of powerschool assignment resolution
    Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
    Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();

    public SectionAssignmentSync(IPowerSchoolClient powerSchool,
                                   IAPIClient edPanel,
                                   School school,
                                   StudentAssociator studentAssociator,
                                   List<Long> unresolvablePowerStudents,
                                   Section createdSection) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.studentAssociator = studentAssociator;
        this.unresolvablePowerStudents = unresolvablePowerStudents;
        this.createdSection = createdSection;
    }

    @Override
    public ConcurrentHashMap<Long, Assignment> synchCreateUpdateDelete() {
        ConcurrentHashMap<Long, Assignment> source = this.resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Assignment> ed = this.resolveFromEdPanel();
        Iterator<Map.Entry<Long, Assignment>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        ExecutorService executor = Executors.newFixedThreadPool(10);
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Assignment> entry = sourceIterator.next();
            Assignment sourceAssignment = entry.getValue();
            Assignment edPanelAssignment = ed.get(entry.getKey());
            if(null == edPanelAssignment){
                Assignment created = edPanel.createSectionAssignment(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        sourceAssignment);
                sourceAssignment.setId(created.getId());
            } else {
                sourceAssignment.setId(edPanelAssignment.getId());
                if(!edPanelAssignment.equals(sourceAssignment)) {
                    edPanel.replaceSectionAssignment(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            sourceAssignment);
                }
            }
            //Regardless of wheter or not we're creating, updating or no-op-ing, sync the StudentAssignments
            StudentAssignmentSyncRunnable runnable = new StudentAssignmentSyncRunnable(
                        powerSchool,
                        edPanel,
                        school,
                        createdSection,
                        sourceAssignment,
                        ssidToStudent
            );
            executor.execute(runnable);
        }
        executor.shutdown();
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Assignment>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Assignment> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                edPanel.deleteSectionAssignment(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        entry.getValue());
            }
        }
        //Spin while we wait for all the threads to complete
        while(!executor.isTerminated()){}
        return source;
    }

    protected ConcurrentHashMap<Long, Assignment> resolveAllFromSourceSystem() {
        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
        PGAssignmentTypes powerTypes =
                powerSchool.getAssignmentTypesBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        if(null != powerTypes && null != powerTypes.record) {
            for (PGAssignmentType pat: powerTypes.record) {
                if(null != pat.tables && null != pat.tables.pgcategories) {
                    typeIdToType.put(
                            Long.valueOf(pat.tables.pgcategories.getId()),
                            pat.tables.pgcategories);
                }
            }
        }
        //Now iterate over all the assignments and construct the correct type of EdPanel assignment
        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        //Get the association between student section score ID and student ID
        SectionScoreIdsResponse ssids = powerSchool.getStudentScoreIdsBySectionId(
                Long.valueOf(createdSection.getSourceSystemId()));
        if(null != ssids && null != ssids.record) {
            for(PsSectionScoreIds ssid: ssids.record) {
                PsSectionScoreId i = ssid.tables.sectionscoresid;
                Long ssidId = Long.valueOf(i.getDcid());
                Student stud = studentAssociator.findBySourceSystemId(Long.valueOf(i.getStudentid()));
                if(null == stud) {
                    stud = migrateMissingStudent(school.getId(), Long.valueOf(i.getStudentid()));
                }
                if(null != stud) {
                    ssidToStudent.put(ssidId, new MutablePair<>(stud, i));
                } else {
                    //TODO: log? no op?
                }
            }
        }

        //THREADING BU SECTION ASSIGNMENT -> STUDENT ASSIGNMENT
        ConcurrentHashMap<Long, Assignment> source = new ConcurrentHashMap<>();
        if(null != powerAssignments && null != powerAssignments.record) {
            for (PGAssignment powerAssignment : powerAssignments.record) {
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

    protected ConcurrentHashMap<Long, Assignment> resolveFromEdPanel() {
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

    /**
     * Sadly, it is possible for a student not returned by the PowerSchool API /schools/:id/students
     * to end up enrolled in a Section. In these cases, we need to go fetch the student and create
     * him/her/it/them ad hoc in edpanel in order to enroll them in the section.  Returns null if the
     * user cannot be retrieved from PowerSchool.
     * @param schoolId
     * @param powerSchoolStudentId
     */
    private Student migrateMissingStudent(Long schoolId, Long powerSchoolStudentId) {
        StudentResponse powerStudent = null;

        try {
            powerStudent = powerSchool.getStudentById(powerSchoolStudentId);
        } catch(HttpClientException e) {
            //Cache the unresolvable student ID for error reporting
            if(null == unresolvablePowerStudents) {
                unresolvablePowerStudents = Collections.synchronizedList(new ArrayList<>());
            }
            unresolvablePowerStudents.add(powerSchoolStudentId);
            return null;
        }
        PsStudents students = new PsStudents();
        students.add(powerStudent.student);
        Collection<Student> studs = students.toInternalModel();
        for(Student edpanelStudent : studs) {
            edpanelStudent.setCurrentSchoolId(schoolId);
            Student createdStudent = edPanel.createStudent(edpanelStudent);
            ConcurrentHashMap<Long, Student> studMap = new ConcurrentHashMap<>();
            try {
                Long otherId = Long.valueOf(createdStudent.getSourceSystemUserId());
                Long ssid = Long.valueOf(createdStudent.getSourceSystemId());
                studentAssociator.associateIds(ssid, otherId);
                studMap.put(otherId, createdStudent);
                studentAssociator.addOtherIdMap(studMap);
            } catch(NumberFormatException e) {
                //NO OP
            }
            return createdStudent;
        }
        return null;
    }
}
