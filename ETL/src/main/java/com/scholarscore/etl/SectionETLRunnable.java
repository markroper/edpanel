package com.scholarscore.etl;

import com.google.gson.JsonSyntaxException;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.PsSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentFactory;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScores;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIds;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentType;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrade;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrades;
import com.scholarscore.etl.powerschool.api.response.AssignmentScoresResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionGradesResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/25/15.
 */
public class SectionETLRunnable implements Runnable {
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private ConcurrentHashMap<Long, User> staff;
    private ConcurrentHashMap<Long, Student> students;
    private ConcurrentHashMap<Long, Section> sections;
    private List<Long> unresolvablePowerStudents;

    public SectionETLRunnable(IPowerSchoolClient powerSchool,
                              IAPIClient edPanel,
                              School school,
                              ConcurrentHashMap<Long, Course> courses,
                              ConcurrentHashMap<Long, Term> terms,
                              ConcurrentHashMap<Long, User> staff,
                              ConcurrentHashMap<Long, Student> students,
                              ConcurrentHashMap<Long, Section> sections,
                              List<Long> unresolvablePowerStudents) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.courses = courses;
        this.terms = terms;
        this.staff = staff;
        this.students = students;
        this.sections = sections;
        this.unresolvablePowerStudents = unresolvablePowerStudents;
    }
    @Override
    public void run() {
        String sourceSystemIdString =school.getSourceSystemId();
        Long sourceSystemSchoolId = new Long(sourceSystemIdString);
        SectionResponse sr = powerSchool.getSectionsBySchoolId(sourceSystemSchoolId);
        if(null != sr && null != sr.sections && null != sr.sections.section) {
            List<PsSection> powerSchoolSections
                    = sr.sections.section;
            for(PsSection powerSection: powerSchoolSections) {
                //Create an EdPanel section
                Section edpanelSection = new Section();
                edpanelSection.setSourceSystemId(powerSection.getId().toString());

                //Resolve the EdPanel Course and set it on the EdPanel section
                Course c = this.courses.get(Long.valueOf(powerSection.getCourse_id()));
                if(null != c) {
                    edpanelSection.setCourse(c);
                    edpanelSection.setName(c.getName());
                }

                //Resolve the EdPanel Term and set it on the Section
                Term sectionTerm = this.terms.get(powerSection.getTerm_id());
                edpanelSection.setTerm(sectionTerm);
                edpanelSection.setStartDate(sectionTerm.getStartDate());
                edpanelSection.setEndDate(sectionTerm.getEndDate());

                //Resolve the EdPanel Teacher(s) and set on the Section
                User t = this.staff.get(powerSection.getStaff_id());
                if(null != t && t instanceof Teacher) {
                    HashSet<Teacher> teachers = new HashSet<>();
                    teachers.add((Teacher) t);
                    edpanelSection.setTeachers(teachers);
                }

                //CREATE THE SECTION WITHIN EDPANEL:
                //TODO: Resolve the grade formula and set it on the Section
                Section createdSection = edPanel.createSection(
                       school.getId(),
                        edpanelSection.getTerm().getSchoolYear().getId(),
                        edpanelSection.getTerm().getId(),
                        edpanelSection);
                this.sections.put(new Long(createdSection.getSourceSystemId()), createdSection);

                //For each section, resolve the enrolled students, migrate them, and migrate the section grades, if any
                migrateStudentSectionEnrollmentAndGrades(powerSection, sectionTerm, createdSection);
                migrateStudentAssignmentGrades(powerSection, sectionTerm, createdSection);
            }
        }
    }

    private void migrateStudentSectionEnrollmentAndGrades(PsSection powerSection, Term sectionTerm, Section createdSection) {
        //CREATE ENROLLED STUDENTS' STUDENTSECTIONGRADE INSTANCES
        //Resolve enrolled students & Create an EdPanel StudentSectionGrade for each
        SectionEnrollmentsResponse enrollments = null;
        try {
            enrollments = powerSchool.getEnrollmentBySectionId(powerSection.getId());
        } catch(JsonSyntaxException e) {
            //TODO: if a single record comes back, PowerSchool doesn't send an array and the marshalling fails :(
            System.out.println("failed to unmarshall section enrollments: " + e.getMessage());
        }
        List<StudentSectionGrade> ssgs = Collections.synchronizedList(new ArrayList<>());
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
                Student edpanelStudent = this.students.get(se.getStudent_id());
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
                    //CREATE THE ENROLLMENT IN EDPANEL
                    StudentSectionGrade createdSsg = null;
                    try {
                        createdSsg = edPanel.createStudentSectionGrade(
                                school.getId(),
                                sectionTerm.getSchoolYear().getId(),
                                sectionTerm.getId(),
                                createdSection.getId(),
                                edpanelStudent.getId(),
                                ssg);
                        ssgs.add(createdSsg);
                    } catch (HttpClientException e) {
                        System.out.println("failed to create ssg!");
                    }
                }
            }
        }
        createdSection.setStudentSectionGrades(ssgs);
    }

    private void migrateStudentAssignmentGrades(PsSection powerSection, Term sectionTerm, Section createdSection) {
        //CREATE THE ASSIGNMENTS FOR THE SECTION:
        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
        PGAssignmentTypes powerTypes = powerSchool.getAssignmentTypesBySectionId(powerSection.getId());
        Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();
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
        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(powerSection.getId());

        //Get the association between student section score ID and student ID
        SectionScoreIdsResponse ssids = powerSchool.getStudentScoreIdsBySectionId(
                Long.valueOf(createdSection.getSourceSystemId()));
        Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
        if(null != ssids && null != ssids.record) {
            for(PsSectionScoreIds ssid: ssids.record) {
                PsSectionScoreId i = ssid.tables.sectionscoresid;
                Long ssidId = Long.valueOf(i.getDcid());
                Student stud = this.students.get(Long.valueOf(i.getStudentid()));
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
        //Create each assignment in EdPanel and all student scores for each assignment
        if(null != powerAssignments && null != powerAssignments.record) {
            for (PGAssignment powerAssignment : powerAssignments.record) {
                PsAssignment pa = powerAssignment.tables.pgassignments;
                Assignment edpanelAssignment = PsAssignmentFactory.fabricate(
                        pa,
                        typeIdToType.get(Long.valueOf(pa.getPgcategoriesid())));
                edpanelAssignment.setSection(createdSection);
                edpanelAssignment.setSectionFK(createdSection.getId());
                Assignment createdAssignment = edPanel.createSectionAssignment(
                        school.getId(),
                        sectionTerm.getSchoolYear().getId(),
                        sectionTerm.getId(),
                        createdSection.getId(),
                        edpanelAssignment);
                //Retrieve students' scores
                AssignmentScoresResponse assScores =
                        powerSchool.getStudentScoresByAssignmentId(Long.valueOf(pa.getDcid()));
                if (null != assScores && null != assScores.record) {

                    List<StudentAssignment> studentAssignmentsToCreate = Collections.synchronizedList(new ArrayList<>());

                    for (PsAssignmentScores sc : assScores.record) {
                        PsScore score = sc.tables.sectionscoresassignments;
                        StudentAssignment studAss = new StudentAssignment();
                        studAss.setAssignment(createdAssignment);
                        //Resolve the student, or move on
                        MutablePair<Student, PsSectionScoreId> sectionScoreIdAndStudent =
                                ssidToStudent.get(Long.valueOf(score.getFdcid()));
                        if (null != sectionScoreIdAndStudent) {
                            studAss.setStudent(sectionScoreIdAndStudent.getLeft());
                        } else {
                            continue;
                        }
                        //Resolve the points
                        Double awardedPoints = null;
                        try {
                            awardedPoints = Double.valueOf(score.getScore());
                        } catch (NumberFormatException e) {
                            //NO OP
                        }
                        if (null == awardedPoints) {
                            studAss.setCompleted(false);
                        } else {
                            studAss.setAwardedPoints(awardedPoints);
                            studAss.setCompleted(true);
                        }
                        studentAssignmentsToCreate.add(studAss);
                    }
                    //We've now generated an EdPanel StudentAssignment for each PS student assignment
                    //Call the bulk create for student assignments for this assignment.
                    if (!studentAssignmentsToCreate.isEmpty()) {
                        edPanel.createStudentAssignments(
                                school.getId(),
                                sectionTerm.getSchoolYear().getId(),
                                sectionTerm.getId(),
                                createdSection.getId(),
                                createdAssignment.getId(),
                                studentAssignmentsToCreate);
                    }
                }
            }
        }
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
            this.students.put(powerSchoolStudentId, createdStudent);
            return createdStudent;
        }
        return null;
    }
}
