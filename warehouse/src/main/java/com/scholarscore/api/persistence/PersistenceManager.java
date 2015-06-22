package com.scholarscore.api.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.persistence.mysql.AuthorityPersistence;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.api.persistence.mysql.StudentSectionGradePersistence;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.api.persistence.mysql.UserPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.*;

public class PersistenceManager implements StudentManager, SchoolManager, SchoolYearManager, 
        TermManager, SectionManager, AssignmentManager, StudentAssignmentManager,
        StudentSectionGradeManager, CourseManager, TeacherManager, UserManager {
    
    private static final String SCHOOL = "school";
    private static final String COURSE = "course";
    private static final String SCHOOL_YEAR = "school year";
    private static final String TERM = "term";
    private static final String SECTION = "section";
    private static final String SECTION_ASSIGNMENT = "section assignment";
    private static final String STUDENT_ASSIGNMENT = "student assignment";
    private static final String STUDENT = "student";
    private static final String STUDENT_SECTION_GRADE = "student section grade";
    private static final String USER = "user";
 
    //Persistence managers for each entity
    private SchoolPersistence schoolPersistence;
    private EntityPersistence<SchoolYear> schoolYearPersistence;
    private EntityPersistence<Term> termPersistence;
    private StudentPersistence studentPersistence;
    private TeacherPersistence teacherPersistence;
    private EntityPersistence<Section> sectionPersistence;
    private EntityPersistence<Course> coursePersistence;
    private EntityPersistence<Assignment> assignmentPersistence;
    private EntityPersistence<StudentAssignment> studentAssignmentPersistence;
    private StudentSectionGradePersistence studentSectionGradePersistence;
    private UserPersistence userPersistence;
    private AuthorityPersistence authorityPersistence;
    
    //Setters for the persistence layer for each entity
    public void setTeacherPersistence(TeacherPersistence ap) {
        this.teacherPersistence = ap;
    }
    
    public void setStudentSectionGradePersistence(StudentSectionGradePersistence ap) {
        this.studentSectionGradePersistence = ap;
    }
    
    public void setStudentAssignmentPersistence(EntityPersistence<StudentAssignment> ap) {
        this.studentAssignmentPersistence = ap;
    }
    
    public void setAssignmentPersistence(EntityPersistence<Assignment> ap) {
        this.assignmentPersistence = ap;
    }
    
    public void setCoursePersistence(EntityPersistence<Course> cp) {
        coursePersistence = cp;
    }
    
    public void setSectionPersistence(EntityPersistence<Section> sectionPersistence) {
        this.sectionPersistence = sectionPersistence;
    }
    
    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }
    
    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }

    public void setSchoolYearPersistence(EntityPersistence<SchoolYear> schoolYearPersistence) {
        this.schoolYearPersistence = schoolYearPersistence;
    }

    public void setTermPersistence(EntityPersistence<Term> termPersistence) {
        this.termPersistence = termPersistence;
    }

 
    //SCHOOLS
    @Override
    public Collection<School> getAllSchools() {
        Collection<School> schools = schoolPersistence.selectAll();
        if(null != schools) {
            for(School s : schools) {
                ServiceResponse<Collection<SchoolYear>> sr = 
                        getAllSchoolYears(s.getId());
                if(null != sr.getValue() && !sr.getValue().isEmpty()) {
                    s.setYears(new ArrayList<SchoolYear>(sr.getValue()));
                }    
            }
        }
        return schools;
    }

    @Override
    public StatusCode schoolExists(long schoolId) {
        School school = schoolPersistence.selectSchool(schoolId);
        if(null == school) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<School>(code);
        }
        School school = schoolPersistence.selectSchool(schoolId);
        ServiceResponse<Collection<SchoolYear>> years = getAllSchoolYears(schoolId);
        if(null != years.getValue() && !years.getValue().isEmpty()) {
            school.setYears(new ArrayList<SchoolYear>(years.getValue()));
        }
        return new ServiceResponse<School>(school);
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        Long schoolId = schoolPersistence.createSchool(school);
        if(null != school.getYears()) {
            for(SchoolYear year : school.getYears()) {
                createSchoolYear(schoolId, year);
            }
        }
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Resolve the set of previously existing terms
        Collection<SchoolYear> originalYears = schoolYearPersistence.selectAll(schoolId);
        HashSet<Long> termIds = new HashSet<>();
        if(null != originalYears) {
            for(SchoolYear t : originalYears) {
                termIds.add(t.getId());
            }
        }
        if(null != school.getYears()) {
            //Insert or update terms on the school year
            for(SchoolYear t : school.getYears()) {
                if(null == t.getId() || !termIds.contains(t.getId())) {
                    createSchoolYear(schoolId, t);
                } else  {
                    termIds.remove(t.getId());
                    replaceSchoolYear(schoolId, t.getId(), t);
                }
            }
        }   
        //Remove remaining terms
        for(Long id : termIds) {
            deleteSchoolYear(schoolId, id);
        }
        
        return new ServiceResponse<Long>(
                schoolPersistence.replaceSchool(schoolId, school));
    }
    
    @Override
    public ServiceResponse<Long> updateSchool(long schoolId, School partialSchool) {
        ServiceResponse<School> sr = getSchool(schoolId);
        if(null == sr.getValue()) {
            return new ServiceResponse<Long>(sr.getCode());
        }
        partialSchool.mergePropertiesIfNull(schoolPersistence.selectSchool(schoolId));
        replaceSchool(schoolId, partialSchool);
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Only need to delete the parent row, FK cascades deletes
        schoolPersistence.delete(schoolId);
        return new ServiceResponse<Long>((Long) null);
    }
    
    //SCHOOL YEARS
    @Override
    public ServiceResponse<Collection<SchoolYear>> getAllSchoolYears(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<SchoolYear>>(code);
        }
        Collection<SchoolYear> schoolYears = schoolYearPersistence.selectAll(schoolId);
        for(SchoolYear year : schoolYears) {
            ServiceResponse<Collection<Term>> sr = getAllTerms(schoolId, year.getId());
            if(null != sr.getValue() && !sr.getValue().isEmpty()) {
                year.setTerms(new ArrayList<Term>(sr.getValue()));
            }
        }
        return new ServiceResponse<Collection<SchoolYear>>(schoolYears);
    }

    @Override
    public StatusCode schoolYearExists(long schoolId, long schoolYearId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return code;
        }
        SchoolYear schoolYear =  schoolYearPersistence.select(schoolId, schoolYearId);
        if(null == schoolYear) {
            return StatusCodes.getStatusCode(
                    StatusCodeType.MODEL_NOT_FOUND, 
                    new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<SchoolYear> getSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<SchoolYear>(code);
        }
        SchoolYear year = schoolYearPersistence.select(schoolId, schoolYearId);
        ServiceResponse<Collection<Term>> terms = getAllTerms(schoolId, schoolYearId);
        if(null != terms.getValue() && !terms.getValue().isEmpty()) {
            year.setTerms(new ArrayList<Term>(terms.getValue()));
        }
        return new ServiceResponse<SchoolYear>(year);
    }

    @Override
    public ServiceResponse<Long> createSchoolYear(long schoolId, SchoolYear schoolYear) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long schoolYearId = schoolYearPersistence.insert(schoolId, schoolYear);
        if(null != schoolYear.getTerms()) {
            for(Term t: schoolYear.getTerms()) {
                createTerm(schoolId, schoolYearId, t);
            }
        }
        return new ServiceResponse<Long>(schoolYearId);
    }

    @Override
    public ServiceResponse<Long> replaceSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        
        //Resolve the set of previously existing terms
        Collection<Term> originalTerms = termPersistence.selectAll(schoolYearId);
        HashSet<Long> termIds = new HashSet<>();
        if(null != originalTerms) {
            for(Term t : originalTerms) {
                termIds.add(t.getId());
            }
        }
        if(null != schoolYear.getTerms()) {
            //Insert or update terms on the school year
            for(Term t : schoolYear.getTerms()) {
                if(null == t.getId() || !termIds.contains(t.getId())) {
                    createTerm(schoolId, schoolYearId, t);
                } else  {
                    termIds.remove(t.getId());
                    replaceTerm(schoolId, schoolYearId, t.getId(), t);
                }
            }
        }   
        //Remove remaining terms
        for(Long id : termIds) {
            deleteTerm(schoolId, schoolYearId, id);
        }
        schoolYearPersistence.update(schoolId, schoolYearId, schoolYear);
        return new ServiceResponse<Long>(schoolYearId);
    }
    
    @Override
    public ServiceResponse<Long> updateSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        schoolYear.setId(schoolYearId);
        SchoolYear originalYear = 
                schoolYearPersistence.select(schoolId, schoolYearId);
        originalYear.setTerms(new ArrayList<Term>(
                termPersistence.selectAll(schoolYearId)));
        schoolYear.mergePropertiesIfNull(originalYear);
        return replaceSchoolYear(schoolId, schoolYearId, schoolYear);
    }

    @Override
    public ServiceResponse<Long> deleteSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Only need to delete the parent record, our deletes cascade
        schoolYearPersistence.delete(schoolYearId);
        return new ServiceResponse<Long>((Long) null);
    }
  
    //TERMS
    @Override
    public ServiceResponse<Collection<Term>> getAllTerms(long schoolId, long yearId) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Term>>(code);
        }
        Collection<Term> terms = termPersistence.selectAll(yearId);
        return new ServiceResponse<Collection<Term>>(new ArrayList<Term>(terms));
    }

    @Override
    public StatusCode termExists(long schoolId, long yearId, long termId) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return code;
        }
        Term t = termPersistence.select(yearId, termId);
        if(null != t) {
            return StatusCodes.getStatusCode(StatusCodeType.OK);
        }
        return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { PersistenceManager.TERM, termId });
    }

    @Override
    public ServiceResponse<Term> getTerm(long schoolId, long yearId, long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Term>(code);
        }
        return new ServiceResponse<Term>(termPersistence.select(yearId, termId));
    }

    @Override
    public ServiceResponse<Long> createTerm(long schoolId, long yearId, Term term) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(termPersistence.insert(yearId, term));
    }

    @Override
    public ServiceResponse<Long> replaceTerm(long schoolId, long yearId, long termId, Term term) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        termPersistence.update(yearId, termId, term);
        return new ServiceResponse<Long>(termId);
    }
    
    @Override
    public ServiceResponse<Long> updateTerm(long schoolId, long yearId,
            long termId, Term partialTerm) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        partialTerm.mergePropertiesIfNull(termPersistence.select(yearId, termId));
        return replaceTerm(schoolId, yearId, termId, partialTerm);
    }

    @Override
    public ServiceResponse<Long> deleteTerm(long schoolId, long yearId,
            long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        termPersistence.delete(termId);
        return new ServiceResponse<Long>((Long) null);
    }

    //Student
    @Override
    public ServiceResponse<Long> createStudent(Student student) {
        return new ServiceResponse<Long>(studentPersistence.createStudent(student));
    }

    @Override
    public StatusCode studentExists(long studentId) {
        Student stud = studentPersistence.select(studentId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Long> deleteStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentPersistence.delete(studentId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Student>> getAllStudents() {
        return new ServiceResponse<Collection<Student>>(
                studentPersistence.selectAll());
    }

    @Override
    public ServiceResponse<Student> getStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Student>(code);
        }
        return new ServiceResponse<Student>(studentPersistence.select(studentId));
    }

    @Override
    public ServiceResponse<Long> replaceStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentPersistence.replaceStudent(studentId, student);
        return new ServiceResponse<Long>(studentId);
    }
    
    @Override
    public ServiceResponse<Long> updateStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        student.setId(studentId);
        student.mergePropertiesIfNull(studentPersistence.select(studentId));
        replaceStudent(studentId, student);
        return new ServiceResponse<Long>(studentId);
    }
    
    //SECTION
    /**
     * Returns all sections in a given school term, with all section assignments
     * and enrolled students populated on the instance.
     * 
     * @param schoolId
     * @param yearId
     * @param termId
     * @return
     */
    @Override
    public ServiceResponse<Collection<Section>> getAllSections(long schoolId, long yearId, long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        Collection<Section> sections = sectionPersistence.selectAll(termId);
        for(Section s : sections) {
            Collection<Student> students = studentPersistence.selectAllStudentsInSection(s.getId());
            if(null != students && !students.isEmpty()) {
                s.setEnrolledStudents(new ArrayList<Student>(students));
            }
            Collection<Assignment> assignments = assignmentPersistence.selectAll(s.getId());
            if(null != assignments && !assignments.isEmpty()) {
                s.setAssignments(new ArrayList<Assignment>(assignments));
            }
        }
        return new ServiceResponse<Collection<Section>>(sections);
    }

    @Override
    public StatusCode sectionExists(long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return code;
        }
        Section section = sectionPersistence.select(termId, sectionId);
        if(null == section) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sectionId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    /**
     * Returns a section with its enrolled students and section assignments populated
     * 
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @return
     */
    @Override
    public ServiceResponse<Section> getSection(long schoolId, long yearId,
            long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Section>(code);
        }
        Section section = sectionPersistence.select(termId, sectionId);
        Collection<Student> students = studentPersistence.selectAllStudentsInSection(sectionId);
        if(null != students && !students.isEmpty()) {
            section.setEnrolledStudents(new ArrayList<Student>(students));
        }
        Collection<Assignment> assignments = assignmentPersistence.selectAll(sectionId);
        if(null != assignments && !assignments.isEmpty()) {
            section.setAssignments(new ArrayList<Assignment>(assignments));
        }
        return new ServiceResponse<Section>(section);
    }

    /**
     * Creates a section including enrolling any students in the enrolled student 
     * set provided as a member of the Section instance provided.  Adding 
     *  section assignments via this endpoint is not supported by this method
     *  at this time.  Any assignments on the instance provided will be ignored.
     *  
     * @param schoolId
     * @param yearId
     * @param termId
     * @param section
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ServiceResponse<Long> createSection(long schoolId, long yearId,
            long termId, Section section) throws JsonProcessingException {
        //TODO: add support for creating section assignments that are on the provided instance
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long sectionId = sectionPersistence.insert(termId, section);     
        if(null != section.getEnrolledStudents()) {
            for(Student s : section.getEnrolledStudents()) {
                if(null != s.getId() && 
                        studentExists(s.getId()).isOK()) {
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    studentSectionGradePersistence.insert(sectionId, s.getId(), ssg);
                } else {
                    sectionPersistence.delete(sectionId);
                    throw new RuntimeException("Invalid section contained enrolled students that don't exist");
                    //TODO: return error to user and remove this exception
                }
            }
        }
        return new ServiceResponse<Long>(sectionId);
    }

    /**
     * When a section is updated, we are able to update the list of enrolled students, but we do
     * support updating the list of assignments in the section via this method at this time. 
     * For that ADD/UPDATE/DELETE assignment API's can be called.
     * 
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @param section
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ServiceResponse<Long> replaceSection(long schoolId, long yearId,
            long termId, long sectionId, Section section) throws JsonProcessingException {
        //TODO: add support for replacing section assignments that are provided on the instance.
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Collection<Student> students = studentPersistence.selectAllStudentsInSection(sectionId);
        HashSet<Long> studentsInSection = new HashSet<>();
        for(Student s : students) {
            studentsInSection.add(s.getId());
        }
        if(null != section.getEnrolledStudents()) {
            for(Student s : section.getEnrolledStudents()) {
                if(!studentsInSection.contains(s.getId())) { 
                    if(studentExists(s.getId()).isOK()) {
                        //Add existing student to the section
                        studentSectionGradePersistence.insert(
                                sectionId, s.getId(), new StudentSectionGrade());
                    }
                } else {
                    //If the student was already in the section, remove it from the set and do nothing to the DB
                    studentsInSection.remove(s.getId());
                }
            }
        }
        //Remove any students that are no longer in the section
        for(Long id : studentsInSection) {
            studentSectionGradePersistence.delete(sectionId, id);
        }    
        sectionPersistence.update(termId, sectionId, section);
        return new ServiceResponse<Long>(sectionId);
    }

    @Override
    public ServiceResponse<Long> updateSection(long schoolId, long yearId,
            long termId, long sectionId, Section partialSection) throws JsonProcessingException {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        partialSection.mergePropertiesIfNull(sectionPersistence.select(termId, sectionId));
        if(null == partialSection.getEnrolledStudents() || partialSection.getEnrolledStudents().isEmpty()) {
            sectionPersistence.update(termId, sectionId, partialSection);
            return new ServiceResponse<Long>(sectionId);
        } else {
            replaceSection(schoolId, yearId, termId, sectionId, partialSection);
            return new ServiceResponse<Long>(sectionId);
        }
    }

    /**
     * Deleting a section will remove it from the system along with the studentSectionGrade instances
     * associated with the section and the assignments from the section.  This is accomplished in this 
     * implementation via foreign key constraints with CASCADE DELETE turned on.
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @return
     */
    @Override
    public ServiceResponse<Long> deleteSection(long schoolId, long yearId,
            long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        sectionPersistence.delete(sectionId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Assignment>> getAllAssignments(
            long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Assignment>>(code);
        }
        return new ServiceResponse<Collection<Assignment>>(assignmentPersistence.selectAll(sectionId));
    }

    //SECTION ASSIGNMENTS
    @Override
    public StatusCode assignmentExists(long schoolId, long yearId,
            long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return code;
        }
        Assignment assignment = assignmentPersistence.select(sectionId, sectionAssignmentId);
        if(null == assignment) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, 
                    new Object[]{ SECTION_ASSIGNMENT, sectionAssignmentId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Assignment> getAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Assignment>(code);
        }
        return new ServiceResponse<Assignment>(assignmentPersistence.select(sectionId, sectionAssignmentId));
    }

    @Override
    public ServiceResponse<Long> createAssignment(long schoolId,
            long yearId, long termId, long sectionId,
            Assignment sectionAssignment) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(assignmentPersistence.insert(sectionId, sectionAssignment));
    }

    @Override
    public ServiceResponse<Long> replaceAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            Assignment sectionAssignment) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(
                assignmentPersistence.update(sectionId, sectionAssignmentId, sectionAssignment));
    }

    @Override
    public ServiceResponse<Long> updateAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            Assignment sectionAssignment) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        sectionAssignment.mergePropertiesIfNull(assignmentPersistence.select(sectionId, sectionAssignmentId));   
        assignmentPersistence.update(sectionId, sectionAssignmentId, sectionAssignment);
        return new ServiceResponse<Long>(sectionAssignmentId);
    }

    @Override
    public ServiceResponse<Long> deleteAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        assignmentPersistence.delete(sectionAssignmentId);
        return new ServiceResponse<Long>((Long) null);
    }

    //STUDENT ASSIGNMENTS
    @Override
    public ServiceResponse<Collection<StudentAssignment>> getAllStudentAssignments(
            long schoolId, long yearId, long termId, long sectionId,
            long sectAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<StudentAssignment>>(code);
        }
        Collection<StudentAssignment> sas = studentAssignmentPersistence.selectAll(sectAssignmentId);
        return new ServiceResponse<Collection<StudentAssignment>>(sas);
    }

    @Override
    public StatusCode studentAssignmentExists(long schoolId, long yearId,
            long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return code;
        }
        StudentAssignment sa = studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId);
        if(null == sa) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{ STUDENT_ASSIGNMENT, studentAssignmentId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<StudentAssignment> getStudentAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId, long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<StudentAssignment>(code);
        }
        StudentAssignment sa = studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId);
        return new ServiceResponse<StudentAssignment>(sa);
    }

    @Override
    public ServiceResponse<Long> createStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            StudentAssignment studentAssignment) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(
                studentAssignmentPersistence.insert(sectionAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> replaceStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(studentAssignmentPersistence.update(
                sectionAssignmentId, studentAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> updateStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentAssignment.setId(studentAssignmentId);
        studentAssignment.mergePropertiesIfNull(
                studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId));
        return new ServiceResponse<Long>(
                studentAssignmentPersistence.update(sectionAssignmentId, studentAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> deleteStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentAssignmentPersistence.delete(studentAssignmentId);
        return new ServiceResponse<Long>((Long) null);
    }

    //STUDENT SECTION GRADE
    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getAllStudentSectionGrades(
            long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentSectionGradePersistence.selectAll(sectionId));
    }

    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getSectionGradesForStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Collection<StudentSectionGrade> grades = studentSectionGradePersistence.selectAllByStudent(studentId);
        return new ServiceResponse<>(grades);
    }

//    // TODO: move this somewhere.
//    public void calculateGPAForStudent(long studentId) {
//        StatusCode code = studentExists(studentId);
////        if(!code.isOK()) {
////            return new ServiceResponse<>(code);
////        }
//        Collection<StudentSectionGrade> grades = studentSectionGradePersistence.selectAllByStudent(studentId);
//        GradeFormula.calculateAverageGrade(grades);
//    }
    
    @Override
    public StatusCode studentSectionGradeExists(long schoolId, long yearId,
            long termId, long sectionId, long studentId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return code;
        }
        code = studentExists(studentId);
        if(!code.isOK()) {
            return code;
        }
        StudentSectionGrade ssg = studentSectionGradePersistence.select(sectionId, studentId);
        if(null == ssg) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{ PersistenceManager.STUDENT_SECTION_GRADE, 
                    "section id: " + sectionId + ", student id: " + studentId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<StudentSectionGrade> getStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId,
            long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentSectionGradePersistence.select(sectionId, studentId));
    }

    @Override
    public ServiceResponse<Long> createStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId,
            StudentSectionGrade grade) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(
                studentSectionGradePersistence.insert(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Long> replaceStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(
                studentSectionGradePersistence.update(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Long> updateStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        grade.mergePropertiesIfNull(studentSectionGradePersistence.select(sectionId, studentId));
        return new ServiceResponse<>(
                studentSectionGradePersistence.update(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Long> deleteStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentSectionGradePersistence.delete(sectionId, studentId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Course>> getAllCourses(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<Collection<Course>>(
                coursePersistence.selectAll(schoolId));
    }

    @Override
    public StatusCode courseExists(long schoolId, long courseId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return code;
        }
        Course course = coursePersistence.select(schoolId, courseId);
        if(null == course) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { COURSE, courseId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Course> getCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(coursePersistence.select(schoolId, courseId));
    }

    @Override
    public ServiceResponse<Long> createCourse(long schoolId, Course course) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(coursePersistence.insert(schoolId, course));
    }

    @Override
    public ServiceResponse<Long> replaceCourse(long schoolId, long courseId,
            Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        coursePersistence.update(schoolId, courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> updateCourse(long schoolId, long courseId,
            Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        course.mergePropertiesIfNull(coursePersistence.select(schoolId, courseId));
        coursePersistence.update(schoolId, courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> deleteCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        //Only need to delete the parent record, our deletes cascade
        coursePersistence.delete(courseId);
        return new ServiceResponse<Long>((Long) null);
    }

  //Teacher
    @Override
    public ServiceResponse<Long> createTeacher(Teacher teacher) {
        return new ServiceResponse<Long>(teacherPersistence.createTeacher(teacher));
    }

    @Override
    public StatusCode teacherExists(long teacherId) {
        Teacher stud = teacherPersistence.select(teacherId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ STUDENT, teacherId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Long> deleteTeacher(long teacherId) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        teacherPersistence.delete(teacherId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Teacher>> getAllTeachers() {
        return new ServiceResponse<Collection<Teacher>>(
                teacherPersistence.selectAll());
    }

    @Override
    public ServiceResponse<Teacher> getTeacher(long teacherId) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Teacher>(code);
        }
        return new ServiceResponse<Teacher>(teacherPersistence.select(teacherId));
    }

    @Override
    public ServiceResponse<Long> replaceTeacher(long teacherId, Teacher teacher) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        teacherPersistence.replaceTeacher(teacherId, teacher);
        return new ServiceResponse<Long>(teacherId);
    }
    
    @Override
    public ServiceResponse<Long> updateTeacher(long teacherId, Teacher teacher) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        teacher.setId(teacherId);
        teacher.mergePropertiesIfNull(teacherPersistence.select(teacherId));
        replaceTeacher(teacherId, teacher);
        return new ServiceResponse<Long>(teacherId);
    }
    
	public UserPersistence getUserPersistence() {
		return userPersistence;
	}

	public void setUserPersistence(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	public AuthorityPersistence getAuthorityPersistence() {
		return authorityPersistence;
	}

	public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
		this.authorityPersistence = authorityPersistence;
	}

    @Override
    public ServiceResponse<Collection<User>> getAllUsers() {
        return new ServiceResponse<Collection<User>>(
                userPersistence.selectAllUsers());

    }

	@Override
	public StatusCode userExists(String username) {
		User user = userPersistence.selectUser(username);
        if(null == user) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { USER, username});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
	}

	@Override
	public ServiceResponse<User> getUser(String username) {
		User user = userPersistence.selectUser(username);
		if (null != user) {
			return new ServiceResponse<User>(user);
		}
		return new ServiceResponse<User>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { USER, username } ));
	}

	@Override
	public ServiceResponse<String> createUser(User value) {
		return new ServiceResponse<String>(userPersistence.createUser(value));
	}

	@Override
	public ServiceResponse<String> replaceUser(String username, User user) {
		return new ServiceResponse<String>(userPersistence.replaceUser(username, user));
	}

	@Override
	public ServiceResponse<String> updateUser(String username, User user) {
		return new ServiceResponse<String>(userPersistence.replaceUser(username, user));
	}

	@Override
	public ServiceResponse<String> deleteUser(String username) {
		return new ServiceResponse<String>(userPersistence.deleteUser(username));
	}
}
