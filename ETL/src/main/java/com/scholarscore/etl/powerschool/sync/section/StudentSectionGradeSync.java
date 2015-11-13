package com.scholarscore.etl.powerschool.sync.section;

import com.google.common.collect.BiMap;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollmentWrapper;
import com.scholarscore.etl.powerschool.api.model.term.TermAssociator;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.MissingStudentMigrator;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Score;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public class StudentSectionGradeSync implements ISync<StudentSectionGrade> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentSectionGradeSync.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private ConcurrentHashMap<Long, Section> sections;
    private TermAssociator termAssociator;
    private BiMap<Long, Long> ptSectionIdToPsSectionId;
    private Map<Long, Long> ptStudentIdToPsStudentId;
    private Section createdSection;

    public StudentSectionGradeSync(IPowerSchoolClient powerSchool,
                                   IAPIClient edPanel,
                                   School school,
                                   StudentAssociator studentAssociator,
                                   TermAssociator termAssociator,
                                   BiMap<Long, Long> ptSectionIdToPsSectionId,
                                   Map<Long, Long> ptStudentIdToPsStudentId,
                                   Section createdSection) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.studentAssociator = studentAssociator;
        this.termAssociator = termAssociator;
        this.ptSectionIdToPsSectionId = ptSectionIdToPsSectionId;
        this.ptStudentIdToPsStudentId = ptStudentIdToPsStudentId;
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
        ConcurrentHashMap<Long, StudentSectionGrade> source = new ConcurrentHashMap<>();
        Long ptSectionId = ptSectionIdToPsSectionId.inverse().get(Long.valueOf(createdSection.getSourceSystemId()));
        if(null != ptSectionId) {
            PsResponse<PtSectionEnrollmentWrapper> ptEnrollmentResp =
                    powerSchool.getPowerTeacherSectionEnrollments(ptSectionId);
            if(null != ptEnrollmentResp && null != ptEnrollmentResp.record) {
                for(PsResponseInner<PtSectionEnrollmentWrapper> enrollWrapper: ptEnrollmentResp.record) {
                    PtSectionEnrollment enrollment = enrollWrapper.tables.psm_sectionenrollment;
                    Long powerSchoolStudentId = ptStudentIdToPsStudentId.get(enrollment.studentid);
                    if(null == powerSchoolStudentId) {
                        LOGGER.warn("Unable to map PowerTeacher studentID with a PowerSchool studentID for enrollment "
                                + enrollment.id + " pt student ID: " + enrollment.studentid);
                        continue;
                    }
                    Student edpanelStudent = studentAssociator.findBySourceSystemId(powerSchoolStudentId);
                    if(null == edpanelStudent) {
                        edpanelStudent = MissingStudentMigrator.resolveMissingStudent(
                                school.getId(),
                                powerSchoolStudentId,
                                powerSchool,
                                edPanel,
                                studentAssociator,
                                results);
                    }
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    ssg.setStudent(edpanelStudent);
                    ssg.setSection(createdSection);
                    //For the enrollment, get all the final scores.
                    HashMap<Long, Score> termScores = new HashMap<>();
                    PsResponse<PtFinalScoreWrapper> scoresResponse = powerSchool.getPowerTeacherFinalScore(enrollment.id);
                    for(PsResponseInner<PtFinalScoreWrapper> scoreWrapper: scoresResponse.record) {
                        PtFinalScore ptScore = scoreWrapper.tables.psm_finalscore;
                        Long powerTeacherReportingTermId = ptScore.reportingtermid;
                        Score score = new Score.ScoreBuilder().
                            withComment(ptScore.commentvalue).
                            withLetterGrade(ptScore.lettergrade).
                            withScore(ptScore.score).
                            withManuallyOverridden("1".equals(ptScore.manualoverride)).
                            withTermId(powerTeacherReportingTermId).
                            build();
                        termScores.put(powerTeacherReportingTermId, score);
                        if(null != createdSection.getGradeFormula() &&
                                createdSection.getGradeFormula().getId().equals(powerTeacherReportingTermId)){
                            ssg.setGrade(score.getScore());
                        }
                    }
                    ssg.setTermGrades(termScores);
                    source.put(powerSchoolStudentId, ssg);
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
