package com.scholarscore.api.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.SectionAssignment;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.SubjectArea;
import com.scholarscore.models.Term;

public class PersistenceManager implements StudentManager, SchoolManager, SchoolYearManager, 
        TermManager, SectionManager, SectionAssignmentManager, StudentAssignmentManager,
        StudentSectionGradeManager, CourseManager, AssignmentManager {
    private static final String SCHOOL = "school";
    private static final String ASSIGNMENT = "assignment";
    private static final String COURSE = "course";
    private static final String SCHOOL_YEAR = "school year";
    private static final String TERM = "term";
    private static final String SECTION = "section";
    private static final String SECTION_ASSIGNMENT = "section assignment";
    private static final String STUDENT_ASSIGNMENT = "student assignment";
    private static final String STUDENT = "student";
    private static final String STUDENT_SECTION_GRADE = "student section grade";

    //Student structure: Map<studentId, Student>
    private final static AtomicLong studentCounter = new AtomicLong();
    private static Map<Long, Student> students = Collections.synchronizedMap(new HashMap<Long, Student>());
    //Student section grade structure: Map<studentId, Map<sectionId, StudentSectionGrade>>
    private final static AtomicLong studentSectGradeCounter = new AtomicLong();
    private static Map<Long, Map<Long, Map<Long, StudentSectionGrade>>> studentSectionGrades =
            Collections.synchronizedMap(new HashMap<Long, Map<Long, Map<Long, StudentSectionGrade>>>());

    //School structure: Map<schoolId, School>
    private final static AtomicLong schoolCounter = new AtomicLong();
    private static Map<Long, School> schools = Collections.synchronizedMap(new HashMap<Long, School>());
    //School year structure: Map<SchoolId, Map<SchoolYearId, SchoolYear>> note: schoolYears contain terms
    private final static AtomicLong schoolYearCounter = new AtomicLong();
    private final static AtomicLong termCounter = new AtomicLong();
    private static Map<Long, Map<Long, SchoolYear>> schoolYears = Collections.synchronizedMap(new HashMap<Long, Map<Long, SchoolYear>>());
    //Map<termId, Map<sectionId, Section>>
    private final static AtomicLong sectionCounter = new AtomicLong();
    private final static Map<Long, Map<Long, Section>> sections = Collections.synchronizedMap(new HashMap<Long, Map<Long, Section>>());

    //Map<SectionId, Map<sectionAssignmentId, SectionAssignment>>
    private final static AtomicLong sectionAssignmentCounter = new AtomicLong();
    //Map<sectionAssignmentId, Map<studentAssignmentId, StudentAssignment>>
    private final static AtomicLong studentAssignmentCounter = new AtomicLong();
    private static Map<Long, Map<Long, StudentAssignment>> studentAssignments =
            Collections.synchronizedMap(new HashMap<Long, Map<Long, StudentAssignment>>());
    //Subject area structure Map<SchoolId, Map<subjectAreaId, SubjectArea>>
    private final static AtomicLong subjectAreaCounter = new AtomicLong();
    private final static Map<Long, Map<Long, SubjectArea>> subjectAreas = Collections.synchronizedMap(new HashMap<Long, Map<Long, SubjectArea>>());
    //Course structure: Map<schoolId, Map<courseId, Course>>
    private final static AtomicLong courseCounter = new AtomicLong();
    private final static Map<Long, Map<Long, Course>> courses = Collections.synchronizedMap(new HashMap<Long, Map<Long, Course>>());
    //Assignments structure: Map<courseId, Map<assignmentId, Assignment>>
    private final static AtomicLong assignmentCounter = new AtomicLong();
    private final static Map<Long, Map<Long, Assignment>> assignments = Collections.synchronizedMap(new HashMap<Long, Map<Long, Assignment>>());
    
    @Override
    public ServiceResponse<Long> createStudent(Student student) {
        student.setId(studentCounter.incrementAndGet());
        students.put(student.getId(), student);
        return new ServiceResponse<Long>(student.getId());
    }

    @Override
    public StatusCode studentExists(long studentId) {
        if(!students.containsKey(studentId)){
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<Long> deleteStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        students.remove(studentId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Student>> getAllStudents() {
        return new ServiceResponse<Collection<Student>>(students.values());
    }

    @Override
    public ServiceResponse<Student> getStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Student>(code, code.getArguments());
        }
        return new ServiceResponse<Student>(students.get(studentId));
    }

    @Override
    public ServiceResponse<Long> replaceStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        student.setId(studentId);
        students.put(student.getId(), student);
        return new ServiceResponse<Long>(studentId);
    }
    
    @Override
    public ServiceResponse<Long> updateStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        student.setId(studentId);
        student.mergePropertiesIfNull(students.get(studentId));
        students.put(student.getId(), student);
        return new ServiceResponse<Long>(studentId);
    }
    
    @Override
    public Collection<School> getAllSchools() {
        return schools.values();
    }

    @Override
    public StatusCode schoolExists(long schoolId) {
        if(!schools.containsKey(schoolId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId }); 
        };
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<School>(code, code.getArguments());
        }
        return new ServiceResponse<School>(schools.get(schoolId));
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        school.setId(schoolCounter.incrementAndGet());
        schools.put(school.getId(), school);
        return new ServiceResponse<Long>(school.getId());
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        school.setId(schoolId);
        schools.put(school.getId(), school);
        return new ServiceResponse<Long>(schoolId);
    }
    
    @Override
    public ServiceResponse<Long> updateSchool(long schoolId, School partialSchool) {
        ServiceResponse<School> sr = getSchool(schoolId);
        if(null == sr.getValue()) {
            return new ServiceResponse<Long>(sr.getCode(), sr.getErrorParams());
        }
        partialSchool.mergePropertiesIfNull(sr.getValue());
        return replaceSchool(schoolId, partialSchool);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schools.remove(schoolId);
        return new ServiceResponse<Long>((Long) null);
    }
    
    @Override
    public ServiceResponse<Collection<SchoolYear>> getAllSchoolYears(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Collection<SchoolYear>>(code, code.getArguments());
        }
        return new ServiceResponse<Collection<SchoolYear>>(schoolYears.get(schoolId).values());
    }

    @Override
    public StatusCode schoolYearExists(long schoolId, long schoolYearId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<SchoolYear> getSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<SchoolYear>(code, code.getArguments());
        }
        return new ServiceResponse<SchoolYear>(schoolYears.get(schoolId).get(schoolYearId));
    }

    @Override
    public ServiceResponse<Long> createSchoolYear(long schoolId, SchoolYear schoolYear) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYear.setId(schoolYearCounter.incrementAndGet());
        if(!schoolYears.containsKey(schoolId)) {
            schoolYears.put(schoolId, new HashMap<Long, SchoolYear>());
        }
        schoolYears.get(schoolId).put(schoolYear.getId(), schoolYear);
        return new ServiceResponse<Long>(schoolYear.getId());
    }

    @Override
    public ServiceResponse<Long> replaceSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYear.setId(schoolYearId);
        //Set up new terms' ids
        SchoolYear originalSchoolYear = schoolYears.get(schoolId).get(schoolYearId);
        HashSet<Long> termIds = new HashSet<>();
        if(null != originalSchoolYear.getTerms()) {
            for(Term t : originalSchoolYear.getTerms()) {
                termIds.add(t.getId());
            }
        }
        if(null != schoolYear.getTerms() && !schoolYear.getTerms().isEmpty()) {
            for(Term t : schoolYear.getTerms()) {
                if(null == t.getId() || !termIds.contains(t.getId())) {
                    t.setId(termCounter.incrementAndGet());
                }
            }
        }
        schoolYears.get(schoolId).put(schoolYearId, schoolYear);
        return new ServiceResponse<Long>(schoolYearId);
    }
    
    @Override
    public ServiceResponse<Long> updateSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYear.setId(schoolYearId);
        schoolYear.mergePropertiesIfNull(getSchoolYear(schoolId, schoolYearId).getValue());
        return replaceSchoolYear(schoolId, schoolYearId, schoolYear);
    }

    @Override
    public ServiceResponse<Long> deleteSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYears.get(schoolId).remove(schoolYearId);
        return new ServiceResponse<Long>((Long) null);
    }
  
    @Override
    public ServiceResponse<Collection<Term>> getAllTerms(long schoolId, long yearId) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Collection<Term>>(code, code.getArguments());
        }
        return new ServiceResponse<Collection<Term>>(
                new ArrayList<Term>(schoolYears.get(schoolId).get(yearId).getTerms()));
    }

    @Override
    public StatusCode termExists(long schoolId, long yearId, long termId) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(schoolYears.containsKey(schoolId) && 
                schoolYears.get(schoolId).containsKey(yearId)) {
            SchoolYear year = schoolYears.get(schoolId).get(yearId);
            Term t = year.findTermById(termId);
            if(null != t) {
                return StatusCodes.OK;
            }
        }
        return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.TERM, termId });
    }

    @Override
    public ServiceResponse<Term> getTerm(long schoolId, long yearId, long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Term>(code, code.getArguments());
        }
        SchoolYear year = PersistenceManager.schoolYears.get(schoolId).get(yearId);
        Term t = year.findTermById(termId);
        return new ServiceResponse<Term>(t);
    }

    @Override
    public ServiceResponse<Long> createTerm(long schoolId, long yearId, Term term) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        term.setId(termCounter.getAndIncrement());
        SchoolYear originalYear = PersistenceManager.schoolYears.get(schoolId).get(yearId);
        if(null == originalYear.getTerms()) {
            originalYear.setTerms(new ArrayList<Term>());
        }
        originalYear.getTerms().add(term);
        return new ServiceResponse<Long>(term.getId());
    }

    @Override
    public ServiceResponse<Long> replaceTerm(long schoolId, long yearId, long termId, Term term) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        SchoolYear originalYear = PersistenceManager.schoolYears.get(schoolId).get(yearId);
        term.setId(termId);
        replaceTerm(originalYear.getTerms(), term);
        return new ServiceResponse<Long>(termId);
    }

    private void replaceTerm(List<Term> terms, Term term) {
        int idx = -1;
        for(int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getId().equals(term.getId())) {
                idx = i;
                break;
            }
        }
        if(idx >= 0) {
            terms.set(idx, term);
        }
    }
    
    @Override
    public ServiceResponse<Long> updateTerm(long schoolId, long yearId,
            long termId, Term partialTerm) {
        StatusCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        SchoolYear originalYear = PersistenceManager.schoolYears.get(schoolId).get(yearId);
        partialTerm.setId(termId);
        partialTerm.mergePropertiesIfNull(originalYear.findTermById(termId));
        replaceTerm(originalYear.getTerms(), partialTerm);
        return new ServiceResponse<Long>(termId);
    }

    @Override
    public ServiceResponse<Long> deleteTerm(long schoolId, long yearId,
            long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        SchoolYear originalYear =  schoolYears.get(schoolId).get(yearId);
        Term termToRemove = originalYear.findTermById(termId);
        originalYear.getTerms().remove(termToRemove);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Section>> getAllSections(long schoolId, long yearId, long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Collection<Section>>(code, code.getArguments());
        }
        Collection<Section> returnSections = new ArrayList<Section>();
        if(sections.containsKey(termId)) {
            returnSections = PersistenceManager.sections.get(termId).values();
        }
        return new ServiceResponse<Collection<Section>>(returnSections);
    }

    @Override
    public StatusCode sectionExists(long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sectionId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<Section> getSection(long schoolId, long yearId,
            long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Section>(code, code.getArguments());
        }
        return new ServiceResponse<Section>(sections.get(termId).get(sectionId));
    }

    @Override
    public ServiceResponse<Long> createSection(long schoolId, long yearId,
            long termId, Section section) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        section.setId(sectionCounter.getAndIncrement());
        if(null == sections.get(termId)) {
            sections.put(termId, new HashMap<Long, Section>());
        }
        sections.get(termId).put(section.getId(), section);
        return new ServiceResponse<Long>(section.getId());
    }

    @Override
    public ServiceResponse<Long> replaceSection(long schoolId, long yearId,
            long termId, long sectionId, Section section) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        section.setId(sectionId);
        sections.get(termId).put(sectionId, section);
        return new ServiceResponse<Long>(sectionId);
    }

    @Override
    public ServiceResponse<Long> updateSection(long schoolId, long yearId,
            long termId, long sectionId, Section partialSection) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        partialSection.setId(sectionId);
        partialSection.mergePropertiesIfNull(sections.get(termId).get(sectionId));
        sections.get(termId).put(sectionId, partialSection);
        return new ServiceResponse<Long>(sectionId);
    }

    @Override
    public ServiceResponse<Long> deleteSection(long schoolId, long yearId,
            long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        sections.get(termId).remove(sectionId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<SectionAssignment>> getAllSectionAssignments(
            long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Collection<SectionAssignment>>(code, code.getArguments());
        }
        Collection<SectionAssignment> returnSections = new ArrayList<>();
        if(null != sections.get(termId).get(sectionId).getSectionAssignments()) {
            returnSections = sections.get(termId).get(sectionId).getSectionAssignments();
        }
        return new ServiceResponse<Collection<SectionAssignment>>(returnSections);
    }

    @Override
    public StatusCode sectionAssignmentExists(long schoolId, long yearId,
            long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                null == sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectionAssignmentId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<SectionAssignment> getSectionAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<SectionAssignment>(code, code.getArguments());
        }
        return new ServiceResponse<SectionAssignment>(
                sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId));
    }

    @Override
    public ServiceResponse<Long> createSectionAssignment(long schoolId,
            long yearId, long termId, long sectionId,
            SectionAssignment sectionAssignment) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        if(null == sections.get(termId).get(sectionId).getSectionAssignments()) {
            sections.get(termId).get(sectionId).setSectionAssignments(new ArrayList<SectionAssignment>());
        } 
        sectionAssignment.setId(sectionAssignmentCounter.getAndIncrement());
        sections.get(termId).get(sectionId).getSectionAssignments().add(sectionAssignment);
        return new ServiceResponse<Long>(sectionAssignment.getId());
    }

    @Override
    public ServiceResponse<Long> replaceSectionAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            SectionAssignment sectionAssignment) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        sectionAssignment.setId(sectionAssignmentId);
        List<SectionAssignment> assignments = sections.get(termId).get(sectionId).getSectionAssignments();
        replaceSectAssignment(assignments, sectionAssignment);
        return new ServiceResponse<Long>(sectionAssignment.getId());
    }

    @Override
    public ServiceResponse<Long> updateSectionAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            SectionAssignment sectionAssignment) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        sectionAssignment.setId(sectionAssignmentId);
        sectionAssignment.mergePropertiesIfNull(sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId));   
        List<SectionAssignment> assignments = sections.get(termId).get(sectionId).getSectionAssignments();
        replaceSectAssignment(assignments, sectionAssignment);
        return new ServiceResponse<Long>(sectionAssignmentId);
    }

    @Override
    public ServiceResponse<Long> deleteSectionAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        SectionAssignment sectAssignment = sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId);;
        sections.get(termId).get(sectionId).getSectionAssignments().remove(sectAssignment);
        return new ServiceResponse<Long>((Long) null);
    }
    
    private void replaceSectAssignment(List<SectionAssignment> assignments, SectionAssignment assignment) {
        int idx = -1;
        for(int i = 0; i < assignments.size(); i++) {
            if(assignments.get(i).getId().equals(assignment.getId())) {
                idx = i;
                break;
            }
        }
        if(idx >= 0) {
            assignments.set(idx, assignment);
        }
    }

    @Override
    public ServiceResponse<Collection<StudentAssignment>> getAllStudentAssignments(
            long schoolId, long yearId, long termId, long sectionId,
            long sectAssignmentId) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Collection<StudentAssignment>>(code, code.getArguments());
        }
        Collection<StudentAssignment> returnSections = new ArrayList<>();
        if(null != PersistenceManager.studentAssignments.get(sectAssignmentId)) {
            returnSections = PersistenceManager.studentAssignments.get(sectAssignmentId).values();
        }
        return new ServiceResponse<Collection<StudentAssignment>>(returnSections);
    }

    @Override
    public StatusCode studentAssignmentExists(long schoolId, long yearId,
            long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!studentAssignments.containsKey(sectionAssignmentId) || 
                !studentAssignments.get(sectionAssignmentId).containsKey(studentAssignmentId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, 
                    new Object[]{ STUDENT_ASSIGNMENT, studentAssignmentId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<StudentAssignment> getStudentAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId, long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<StudentAssignment>(code, code.getArguments());
        }
        return new ServiceResponse<StudentAssignment>(
                studentAssignments.get(sectionAssignmentId).get(studentAssignmentId));
    }

    @Override
    public ServiceResponse<Long> createStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            StudentAssignment studentAssignment) {
        StatusCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        if(null == studentAssignments.get(sectionAssignmentId)) {
            studentAssignments.put(sectionAssignmentId, new HashMap<Long, StudentAssignment>());
        } 
        studentAssignment.setId(studentAssignmentCounter.getAndIncrement());
        studentAssignments.get(sectionAssignmentId).put(studentAssignment.getId(), studentAssignment);
        return new ServiceResponse<Long>(studentAssignment.getId());
    }

    @Override
    public ServiceResponse<Long> replaceStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        studentAssignment.setId(studentAssignmentId);
        PersistenceManager.studentAssignments.get(sectionAssignmentId).put(studentAssignmentId, studentAssignment);
        return new ServiceResponse<Long>(studentAssignmentId);
    }

    @Override
    public ServiceResponse<Long> updateStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        studentAssignment.setId(studentAssignmentId);
        studentAssignment.mergePropertiesIfNull(PersistenceManager.studentAssignments.get(sectionAssignmentId).get(studentAssignmentId));
        studentAssignments.get(sectionAssignmentId).put(studentAssignmentId, studentAssignment);
        return new ServiceResponse<Long>(studentAssignmentId);
    }

    @Override
    public ServiceResponse<Long> deleteStudentAssignment(long schoolId,
            long yearId, long termId, long sectionId, long sectionAssignmentId,
            long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId, 
                sectionAssignmentId, studentAssignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        studentAssignments.get(sectionAssignmentId).remove(studentAssignmentId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getAllStudentSectionGrades(
            long schoolId, long yearId, long termId, long sectionId,
            long studentId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        // TODO Auto-generated method stub
        Collection<StudentSectionGrade> returnGrades = null;
        if(studentSectionGrades.containsKey(studentId) && studentSectionGrades.get(studentId).containsKey(sectionId)) {
            returnGrades = PersistenceManager.studentSectionGrades.get(studentId).get(sectionId).values();
        }
        return new ServiceResponse<>(returnGrades);
    }

    @Override
    public StatusCode studentSectionGradeExists(long schoolId, long yearId,
            long termId, long sectionId, long studentId, long gradeId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!studentSectionGrades.containsKey(studentId) || !studentSectionGrades.get(studentId).containsKey(sectionId) ||
                !studentSectionGrades.get(studentId).get(sectionId).containsKey(gradeId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, 
                    new Object[]{ PersistenceManager.STUDENT_SECTION_GRADE, gradeId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<StudentSectionGrade> getStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId,
            long studentId, long gradeId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId, gradeId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        return new ServiceResponse<>(
                studentSectionGrades.get(studentId).get(sectionId).get(gradeId));
    }

    @Override
    public ServiceResponse<Long> createStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId,
            StudentSectionGrade grade) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        code = studentExists(studentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        if(null == sections.get(termId).get(sectionId).getEnrolledStudents() ||
                null == sections.get(termId).get(sectionId).findEnrolledStudentById(studentId)) {
            return new ServiceResponse<>(StatusCodes.ENTITY_INVALID_IN_CONTEXT, new Object[]{ 
                    STUDENT_SECTION_GRADE, studentId, SECTION, sectionId 
                    });
        }
        if(null == studentSectionGrades.get(studentId)) {
            studentSectionGrades.put(studentId, new HashMap<Long, Map<Long, StudentSectionGrade>>());
        } 
        if(null == studentSectionGrades.get(studentId).get(sectionId)) {
            studentSectionGrades.get(studentId).put(sectionId, new HashMap<Long, StudentSectionGrade>());
        }
        grade.setId(studentSectGradeCounter.incrementAndGet());
        studentSectionGrades.get(studentId).get(sectionId).put(grade.getId(), grade);
        return new ServiceResponse<>(grade.getId());
    }

    @Override
    public ServiceResponse<Long> replaceStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId,
            long gradeId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId, gradeId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        grade.setId(gradeId);
        studentSectionGrades.get(studentId).get(sectionId).put(gradeId, grade);
        return new ServiceResponse<>(gradeId);
    }

    @Override
    public ServiceResponse<Long> updateStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId,
            long gradeId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId, gradeId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        grade.setId(gradeId);
        grade.mergePropertiesIfNull(studentSectionGrades.get(studentId).get(sectionId).get(gradeId));
        studentSectionGrades.get(studentId).get(sectionId).put(gradeId, grade);
        return new ServiceResponse<>(gradeId);
    }

    @Override
    public ServiceResponse<Long> deleteStudentSectionGrade(long schoolId,
            long yearId, long termId, long sectionId, long studentId,
            long gradeId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId, gradeId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        PersistenceManager.studentSectionGrades.get(studentId).get(sectionId).remove(gradeId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Course>> getAllCourses(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        return new ServiceResponse<Collection<Course>>(
                new ArrayList<>(courses.get(schoolId).values()));
    }

    @Override
    public StatusCode courseExists(long schoolId, long courseId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!courses.containsKey(schoolId) || !courses.get(schoolId).containsKey(courseId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { COURSE, courseId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<Course> getCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(courses.get(schoolId).get(courseId));
    }

    @Override
    public ServiceResponse<Long> createCourse(long schoolId, Course course) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        course.setId(courseCounter.incrementAndGet());
        if(!courses.containsKey(schoolId)) {
            courses.put(schoolId, new HashMap<Long, Course>());
        }
        courses.get(schoolId).put(course.getId(), course);
        return new ServiceResponse<>(course.getId());
    }

    @Override
    public ServiceResponse<Long> replaceCourse(long schoolId, long courseId,
            Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        course.setId(courseId);
        courses.get(schoolId).put(courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> updateCourse(long schoolId, long courseId,
            Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        course.setId(courseId);
        course.mergePropertiesIfNull(courses.get(schoolId).get(courseId));
        courses.get(schoolId).put(courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> deleteCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        courses.get(schoolId).remove(courseId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Assignment>> getAllAssignments(
            long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        ArrayList<Assignment> returnAssignments = new ArrayList<Assignment>();
        if(assignments.containsKey(courseId)) {
            returnAssignments = new ArrayList<>(assignments.get(courseId).values());
        }
        return new ServiceResponse<Collection<Assignment>>(returnAssignments);
    }

    @Override
    public StatusCode assignmentExists(long schoolId, long courseId,
            long assignmentId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return code;
        }
        if(!assignments.containsKey(courseId) || 
                !assignments.get(courseId).containsKey(assignmentId)) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { ASSIGNMENT, assignmentId });
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<Assignment> getAssignment(long schoolId,
            long courseId, long assignmentId) {
        StatusCode code = assignmentExists(schoolId, courseId, assignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        return new ServiceResponse<>(assignments.get(courseId).get(assignmentId));
    }

    @Override
    public ServiceResponse<Long> createAssignment(long schoolId, long courseId,
            Assignment assignment) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        assignment.setId(assignmentCounter.getAndIncrement());
        if(!assignments.containsKey(courseId)) {
            assignments.put(courseId, new HashMap<Long, Assignment>());
        }
        assignments.get(courseId).put(assignment.getId(), assignment);
        return new ServiceResponse<>(assignment.getId());
    }

    @Override
    public ServiceResponse<Long> replaceAssignment(long schoolId,
            long courseId, long assignmentId, Assignment assignment) {
        StatusCode code = assignmentExists(schoolId, courseId, assignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        assignment.setId(assignmentId);
        assignments.get(courseId).put(assignmentId, assignment);
        return new ServiceResponse<>(assignmentId);
    }

    @Override
    public ServiceResponse<Long> updateAssignment(long schoolId, long courseId,
            long assignmentId, Assignment assignment) {
        StatusCode code = assignmentExists(schoolId, courseId, assignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        assignment.setId(assignmentId);
        assignment.mergePropertiesIfNull(assignments.get(courseId).get(assignmentId));
        assignments.get(courseId).put(assignmentId, assignment);
        return new ServiceResponse<>(assignmentId);
        
    }

    @Override
    public ServiceResponse<Long> deleteAssignment(long schoolId, long courseId,
            long assignmentId) {
        StatusCode code = assignmentExists(schoolId, courseId, assignmentId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<>(code, code.getArguments());
        }
        PersistenceManager.assignments.get(courseId).remove(assignmentId);
        return new ServiceResponse<>((Long) null);
    }
}
