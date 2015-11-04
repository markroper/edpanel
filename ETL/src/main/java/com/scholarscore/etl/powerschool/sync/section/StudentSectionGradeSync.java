package com.scholarscore.etl.powerschool.sync.section;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.PsSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrade;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.MissingStudentMigrator;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;

import java.io.IOException;
import java.util.ArrayList;
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
    private Section createdSection;

    public StudentSectionGradeSync(IPowerSchoolClient powerSchool,
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
    public ConcurrentHashMap<Long, StudentSectionGrade> syncCreateUpdateDelete(SyncResult results) {
        //To populate and set on the createdSection
        List<StudentSectionGrade> ssgs = Collections.synchronizedList(new ArrayList<>());
        ConcurrentHashMap<Long, StudentSectionGrade> source = null;
        try {
            source = this.resolveAllFromSourceSystem(results);
        } catch (HttpClientException e) {
            results.studentSectionGradeSourceGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(this.createdSection.getSourceSystemId()),
                    this.createdSection.getId()
            );
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, StudentSectionGrade> edpanelSsgMap = null;
        try {
            edpanelSsgMap = this.resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.studentSectionGradeEdPanelGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(this.createdSection.getSourceSystemId()),
                    this.createdSection.getId()
            );
            return new ConcurrentHashMap<>();
        }
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
                results.studentSectionGradeCreated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), -1L);
            } else {
                //Massage the objects to resolve whether or not an update is needed
                sourceSsg.setId(edPanelSsg.getId());
                if(sourceSsg.getStudent().getId().equals(edPanelSsg.getStudent().getId())) {
                    sourceSsg.setStudent(edPanelSsg.getStudent());
                }
                if(sourceSsg.getSection().getId().equals(edPanelSsg.getSection().getId())) {
                    sourceSsg.setSection(edPanelSsg.getSection());
                }
                if(!edPanelSsg.equals(sourceSsg)) {
                    try {
                        edPanel.replaceStudentSectionGrade(
                                school.getId(),
                                createdSection.getTerm().getSchoolYear().getId(),
                                createdSection.getTerm().getId(),
                                createdSection.getId(),
                                sourceSsg.getStudent().getId(),
                                sourceSsg);
                    } catch (IOException e) {
                        results.studentSectionGradeUpdateFailed(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceSsg.getId());
                        continue;
                    }
                    results.studentSectionGradeUpdated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceSsg.getId());
                }
            }
        }
        //Bulk Create those identified as new in the while loop!
        try {
            edPanel.createStudentSectionGrades(
                    school.getId(),
                    createdSection.getTerm().getSchoolYear().getId(),
                    createdSection.getTerm().getId(),
                    createdSection.getId(),
                    ssgsToCreate);
        } catch (HttpClientException e) {
            results.studentSectionGradeCreateFailed(Long.valueOf(createdSection.getSourceSystemId()), createdSection.getId());
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, StudentSectionGrade>> edpanelIterator = edpanelSsgMap.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, StudentSectionGrade> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                StudentSectionGrade edPanelSsg = entry.getValue();
                try {
                    edPanel.deleteStudentSectionGrade(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            edPanelSsg.getStudent().getId(),
                            edPanelSsg);
                } catch (HttpClientException e) {
                    results.studentSectionGradeDeleteFailed(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), edPanelSsg.getId());
                    continue;
                }
                results.studentSectionGradeDeleted(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), edPanelSsg.getId());
            }
        }
        createdSection.setStudentSectionGrades(ssgs);
        return source;
    }

    protected ConcurrentHashMap<Long, StudentSectionGrade> resolveAllFromSourceSystem(SyncResult results) throws HttpClientException {
        //Resolve enrolled students & Create an EdPanel StudentSectionGrade for each
        SectionEnrollmentsResponse enrollments = null;
        enrollments = powerSchool.getEnrollmentBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
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
                PsResponse<PsSectionGradeWrapper> sectScores = null;
                    sectScores = powerSchool.getSectionScoresBySectionId(
                            Long.valueOf(createdSection.getSourceSystemId()));
                if(null != sectScores && null != sectScores.record) {
                    studentIdToSectionScore = new HashMap<>();
                    for(PsResponseInner<PsSectionGradeWrapper> ss: sectScores.record) {
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
                    edpanelStudent = MissingStudentMigrator.resolveMissingStudent(
                            school.getId(),
                            se.getStudent_id(),
                            powerSchool,
                            edPanel,
                            studentAssociator,
                            results);
                }
                if(null != se && null != edpanelStudent) {
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    ssg.setStudent(edpanelStudent);
                    ssg.setSection(createdSection);
                    if(null != studentIdToSectionScore) {
                        PsSectionGrade score = studentIdToSectionScore.get(
                                Long.valueOf(edpanelStudent.getSourceSystemId()));
                        Double pct = null;
                        if(null != score) {
                            pct = score.getPercent();
                        }
                        if(null != score.getExcludefromgpa() && score.getExcludefromgpa().equals("1")) {
                            ssg.setExempt(true);
                        } else {
                            ssg.setExempt(false);
                        }
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

    protected ConcurrentHashMap<Long, StudentSectionGrade> resolveFromEdPanel() throws HttpClientException {
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
}
