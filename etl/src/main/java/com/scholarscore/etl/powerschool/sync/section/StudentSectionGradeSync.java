package com.scholarscore.etl.powerschool.sync.section;

import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollmentWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.MissingStudentMigrator;
import com.scholarscore.etl.powerschool.sync.LongIndexSyncBase;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.grade.Score;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public class StudentSectionGradeSync implements ISync<StudentSectionGrade> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentSectionGradeSync.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private StudentAssociator studentAssociator;
    private BiMap<Long, Long> ptSectionIdToPsSectionId;
    private Map<Long, Long> ptStudentIdToPsStudentId;
    private Section createdSection;
    private Map<Long, Set<Section>> studentClasses;

    public StudentSectionGradeSync(IPowerSchoolClient powerSchool,
                                   IAPIClient edPanel,
                                   School school,
                                   StudentAssociator studentAssociator,
                                   BiMap<Long, Long> ptSectionIdToPsSectionId,
                                   Map<Long, Long> ptStudentIdToPsStudentId,
                                   Section createdSection,
                                   Map<Long, Set<Section>> studentClasses) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.studentAssociator = studentAssociator;
        this.ptSectionIdToPsSectionId = ptSectionIdToPsSectionId;
        this.ptStudentIdToPsStudentId = ptStudentIdToPsStudentId;
        this.createdSection = createdSection;
        this.studentClasses = studentClasses;
    }

    @Override
    public ConcurrentHashMap<Long, StudentSectionGrade> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, StudentSectionGrade> source = null;
        try {
            source = this.resolveAllFromSourceSystem(results);
        } catch (HttpClientException e) {
            try {
                source = this.resolveAllFromSourceSystem(results);
            } catch (HttpClientException ex) {
                LOGGER.warn("Unable to resolve student section grades from PowerSchool for section with name: " +
                        createdSection.getName() +
                        ", ID: " + createdSection.getId() +
                        ", SSID: " + createdSection.getSourceSystemId() +
                        ", & School ID: " + school.getId());
                results.studentSectionGradeSourceGetFailed(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.createdSection.getSourceSystemId()),
                        this.createdSection.getId()
                );
                return new ConcurrentHashMap<>();
            }
        }
        ConcurrentHashMap<Long, StudentSectionGrade> edpanelSsgMap = null;
        try {
            edpanelSsgMap = this.resolveFromEdPanel();
        } catch (HttpClientException e) {
            try {
                edpanelSsgMap = this.resolveFromEdPanel();
            } catch (HttpClientException ex) {
                LOGGER.warn("Unable to resolve student section grades from EdPanel for section with name: " +
                        createdSection.getName() +
                        ", ID: " + createdSection.getId() +
                        ", SSID: " + createdSection.getSourceSystemId() +
                        ", & School ID: " + school.getId());
                results.studentSectionGradeEdPanelGetFailed(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.createdSection.getSourceSystemId()),
                        this.createdSection.getId()
                );
                return new ConcurrentHashMap<>();
            }
        }

        ArrayList<StudentSectionGrade> ssgsToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        for (Map.Entry<Long, StudentSectionGrade> entry : source.entrySet()) {
            StudentSectionGrade sourceSsg = entry.getValue();
            StudentSectionGrade edPanelSsg = edpanelSsgMap.get(entry.getKey());

            if (sourceSsg.getStudent() == null) {
                LOGGER.warn("sourceSsg.getStudent() is Null! Skipping...");
                continue;
            }
            //Generate map of studentId to sections they are enrolled in to be used later for attendance
            if (studentClasses.get(sourceSsg.getStudent().getId()) == null) {
                studentClasses.put(sourceSsg.getStudent().getId(), Sets.newConcurrentHashSet());
            }
            studentClasses.get(sourceSsg.getStudent().getId()).add(sourceSsg.getSection());

            if (null == edPanelSsg) {
                ssgsToCreate.add(sourceSsg);
                results.studentSectionGradeCreated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), -1L);
            } else {
                //Massage the objects to resolve whether or not an update is needed
                sourceSsg.setId(edPanelSsg.getId());
                if (sourceSsg.getStudent().getId().equals(edPanelSsg.getStudent().getId())) {
                    sourceSsg.setStudent(edPanelSsg.getStudent());
                }
                if (null != sourceSsg.getOverallGrade() && null != edPanelSsg.getOverallGrade()) {
                    sourceSsg.getOverallGrade().setId(edPanelSsg.getOverallGrade().getId());
                }
                if (sourceSsg.getSection().getId().equals(edPanelSsg.getSection().getId())) {
                    sourceSsg.setSection(edPanelSsg.getSection());
                }
                if (!edPanelSsg.equals(sourceSsg)) {
                    if (null != sourceSsg.getOverallGrade() &&
                            null != sourceSsg.getOverallGrade().getDate() &&
                            null != edPanelSsg.getOverallGrade() &&
                            !sourceSsg.getOverallGrade().getDate().equals(edPanelSsg.getOverallGrade().getDate())) {
                        sourceSsg.getOverallGrade().setId(null);
                    }
                    try {
                        edPanel.replaceStudentSectionGrade(
                                school.getId(),
                                createdSection.getTerm().getSchoolYear().getId(),
                                createdSection.getTerm().getId(),
                                createdSection.getId(),
                                sourceSsg.getStudent().getId(),
                                sourceSsg);
                    } catch (HttpClientException e) {
                        results.studentSectionGradeUpdateFailed(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceSsg.getId());
                        continue;
                    }
                    results.studentSectionGradeUpdated(Long.valueOf(createdSection.getSourceSystemId()), entry.getKey(), sourceSsg.getId());
                }
            }
        }
        //Bulk Create those identified as new in the while loop!
        try {
            if(!ssgsToCreate.isEmpty()) {
                edPanel.createStudentSectionGrades(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        ssgsToCreate);
            }
        } catch (HttpClientException e) {
            results.studentSectionGradeCreateFailed(Long.valueOf(createdSection.getSourceSystemId()), createdSection.getId());
        }
        //Delete anything IN EdPanel that is NOT in source system
        for (Map.Entry<Long, StudentSectionGrade> entry : edpanelSsgMap.entrySet()) {
            if (!source.containsKey(entry.getKey())) {
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
        return source;
    }

    protected ConcurrentHashMap<Long, StudentSectionGrade> resolveAllFromSourceSystem(PowerSchoolSyncResult results) throws HttpClientException {
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
                    Student edpanelStudent = studentAssociator.findByUserSourceSystemId(powerSchoolStudentId);
                    if(null == edpanelStudent) {
                        edpanelStudent = MissingStudentMigrator.resolveMissingStudent(
                                school.getId(),
                                powerSchoolStudentId,
                                powerSchool,
                                edPanel,
                                studentAssociator,
                                results);
                        if(null == edpanelStudent) {
                            LOGGER.info("Could not find student enrolled in class with student source system ID: " +
                                    powerSchoolStudentId);
                            continue;
                        }
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
                            withManuallyOverridden(Long.valueOf("1").equals(ptScore.manualoverride)).
                            withTermId(powerTeacherReportingTermId).
                            build();
                        termScores.put(powerTeacherReportingTermId, score);
                        if(null != createdSection.getGradeFormula() &&
                                createdSection.getGradeFormula().getId().equals(powerTeacherReportingTermId)){
                            SectionGrade gr = new SectionGrade(score);
                            gr.setSectionFk(createdSection.getId());
                            gr.setStudentFk(edpanelStudent.getId());
                            ssg.setOverallGrade(gr);
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
