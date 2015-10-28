package com.scholarscore.etl.powerschool.sync.section;

import com.google.gson.JsonSyntaxException;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrade;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrades;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionGradesResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public class StudentSectionGradeSync implements ISync<StudentSectionGrade> {

    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private ConcurrentHashMap<Long, Section> sections;
    private List<Long> unresolvablePowerStudents;
    private Section createdSection;

    public StudentSectionGradeSync(IPowerSchoolClient powerSchool,
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
    public ConcurrentHashMap<Long, StudentSectionGrade> synchCreateUpdateDelete() {
        //To populate and set on the createdSection
        List<StudentSectionGrade> ssgs = Collections.synchronizedList(new ArrayList<>());
        ConcurrentHashMap<Long, StudentSectionGrade> source = this.resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, StudentSectionGrade> edpanelSsgMap = this.resolveFromEdPanel();
        Iterator<Map.Entry<Long, StudentSectionGrade>> sourceIterator = source.entrySet().iterator();
        ArrayList<StudentSectionGrade> ssgsToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, StudentSectionGrade> entry = sourceIterator.next();
            StudentSectionGrade sourceSsg = entry.getValue();
            StudentSectionGrade edPanelSsg = edpanelSsgMap.get(entry.getKey());
            ssgs.add(sourceSsg);
            if(null == edPanelSsg){
                ssgsToCreate.add(sourceSsg);
            } else {
                sourceSsg.setId(edPanelSsg.getId());
                if(!edPanelSsg.equals(sourceSsg)) {
                    edPanel.replaceStudentSectionGrade(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            sourceSsg.getStudent().getId(),
                            sourceSsg);
                }
            }
        }
        //Bulk Create those identified as new in the while loop!
        edPanel.createStudentSectionGrades(
                school.getId(),
                createdSection.getTerm().getSchoolYear().getId(),
                createdSection.getTerm().getId(),
                createdSection.getId(),
                ssgsToCreate);
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, StudentSectionGrade>> edpanelIterator = edpanelSsgMap.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, StudentSectionGrade> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                StudentSectionGrade edPanelSsg = entry.getValue();
                edPanel.deleteStudentSectionGrade(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        edPanelSsg.getStudent().getId(),
                        edPanelSsg);
            }
        }
        createdSection.setStudentSectionGrades(ssgs);
        return source;
    }

    protected ConcurrentHashMap<Long, StudentSectionGrade> resolveAllFromSourceSystem() {
        //Resolve enrolled students & Create an EdPanel StudentSectionGrade for each
        SectionEnrollmentsResponse enrollments = null;
        try {
            enrollments = powerSchool.getEnrollmentBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        } catch(JsonSyntaxException e) {
            //TODO: if a single record comes back, PowerSchool doesn't send an array and the marshalling fails :(
            System.out.println("failed to unmarshall section enrollments: " + e.getMessage());
        }
        List<StudentSectionGrade> ssgs = Collections.synchronizedList(new ArrayList<>());
        /*
            RESOLVE SSGS FROM POWERSCHOOL
         */
        ConcurrentHashMap<Long, StudentSectionGrade> source = new ConcurrentHashMap<>();
        if(null != enrollments && null != enrollments.section_enrollments
                && null != enrollments.section_enrollments.section_enrollment) {

            //See if any final grades have been created for the section, and if so, retrieve them
            Map<Long, PsSectionGrade> studentIdToSectionScore = null;
            if(createdSection.getEndDate().compareTo(new Date()) < 0) {
                SectionGradesResponse sectScores = powerSchool.getSectionScoresBySectionId(
                        Long.valueOf(createdSection.getSourceSystemId()));
                if(null != sectScores && null != sectScores.record) {
                    studentIdToSectionScore = new HashMap<>();
                    for(PsSectionGrades ss: sectScores.record) {
                        PsSectionGrade score = ss.tables.storedgrades;
                        studentIdToSectionScore.put(
                                Long.valueOf(score.getStudentid()),
                                score);
                    }
                }
            }
            //Create an EdPanel StudentSectionGrade for each PowerSchool StudentEnrollment
            for(PsSectionEnrollment se : enrollments.section_enrollments.section_enrollment) {
                Student edpanelStudent = studentAssociator.findBySourceSystemId(se.getStudent_id());
                if(null == edpanelStudent) {
                    edpanelStudent = migrateMissingStudent(school.getId(), se.getStudent_id());
                }
                if(null != se && null != edpanelStudent) {
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    ssg.setStudent(edpanelStudent);
                    ssg.setSection(createdSection);
                    if(null != studentIdToSectionScore) {
                        PsSectionGrade score = studentIdToSectionScore.get(
                                Long.valueOf(edpanelStudent.getSourceSystemId()));
                        Double pct = score.getPercent();
                        ssg.setGrade(pct);
                        ssg.setComplete(true);
                    } else {
                        ssg.setComplete(false);
                    }
                    source.put(se.getStudent_id(), ssg);
                }
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, StudentSectionGrade> resolveFromEdPanel() {
        ConcurrentHashMap<Long, StudentSectionGrade> edpanelSsgMap = new ConcurrentHashMap<>();
        StudentSectionGrade[] edPanelSsgs = edPanel.getStudentSectionGrades(
                school.getId(),
                createdSection.getTerm().getSchoolYear().getId(),
                createdSection.getTerm().getId(),
                createdSection.getId());
        for(StudentSectionGrade ssg : edPanelSsgs) {
            edpanelSsgMap.put(
                    Long.valueOf(ssg.getStudent().getSourceSystemId()),
                    ssg
            );
        }
        return edpanelSsgMap;
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
