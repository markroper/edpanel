package com.scholarscore.api.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
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
        TermManager, SectionManager, SectionAssignmentManager {
    public static final String SCHOOL = "school";
    public static final String ASSIGNMENT = "assignment";
    public static final String COURSE = "course";
    public static final String SCHOOL_YEAR = "school year";
    public static final String TERM = "term";
    public static final String SECTION = "section";
    public static final String SECTION_ASSIGNMENT = "section assignment";
    public static final String STUDENT_ASSIGNMENT = "student assignment";
    public static final String STUDENT = "student";
    public static final String STUDENT_SECTION_GRADE = "student section grade";

    // Todo refactor_persistence: everything from here to 75 should be private.
    //Student structure: Map<studentId, Student>
    public final static AtomicLong studentCounter = new AtomicLong();
    public static Map<Long, Student> students = Collections.synchronizedMap(new HashMap<Long, Student>());
    //Student section grade structure: Map<studentId, Map<sectionId, StudentSectionGrade>>
    public final static AtomicLong studentSectGradeCounter = new AtomicLong();
    public static Map<Long, Map<Long, Map<Long, StudentSectionGrade>>> studentSectionGrades =
            Collections.synchronizedMap(new HashMap<Long, Map<Long, Map<Long, StudentSectionGrade>>>());

    //School structure: Map<schoolId, School>
    public final static AtomicLong schoolCounter = new AtomicLong();
    public static Map<Long, School> schools = Collections.synchronizedMap(new HashMap<Long, School>());
    //School year structure: Map<SchoolId, Map<SchoolYearId, SchoolYear>> note: schoolYears contain terms
    public final static AtomicLong schoolYearCounter = new AtomicLong();
    public final static AtomicLong termCounter = new AtomicLong();
    public static Map<Long, Map<Long, SchoolYear>> schoolYears = Collections.synchronizedMap(new HashMap<Long, Map<Long, SchoolYear>>());
    //Map<termId, Map<sectionId, Section>>
    public final static AtomicLong sectionCounter = new AtomicLong();
    public final static Map<Long, Map<Long, Section>> sections = Collections.synchronizedMap(new HashMap<Long, Map<Long, Section>>());

    //Map<SectionId, Map<sectionAssignmentId, SectionAssignment>>
    public final static AtomicLong sectionAssignmentCounter = new AtomicLong();
    //Map<sectionAssignmentId, Map<studentAssignmentId, StudentAssignment>>
    public final static AtomicLong studentAssignmentCounter = new AtomicLong();
    public static Map<Long, Map<Long, StudentAssignment>> studentAssignments = 
            Collections.synchronizedMap(new HashMap<Long, Map<Long, StudentAssignment>>());
    //Subject area structure Map<SchoolId, Map<subjectAreaId, SubjectArea>>
    public final static AtomicLong subjectAreaCounter = new AtomicLong();
    public final static Map<Long, Map<Long, SubjectArea>> subjectAreas = Collections.synchronizedMap(new HashMap<Long, Map<Long, SubjectArea>>());
    //Course structure: Map<schoolId, Map<courseId, Course>>
    public final static AtomicLong courseCounter = new AtomicLong();
    public final static Map<Long, Map<Long, Course>> courses = Collections.synchronizedMap(new HashMap<Long, Map<Long, Course>>());
    //Assignments structure: Map<courseId, Map<assignmentId, Assignment>>
    public final static AtomicLong assignmentCounter = new AtomicLong();
    public final static Map<Long, Map<Long, Assignment>> assignments = Collections.synchronizedMap(new HashMap<Long, Map<Long, Assignment>>());
    
    @Override
    public long createStudent(Student student) {
        student.setId(studentCounter.incrementAndGet());
        students.put(student.getId(), student);
        return student.getId();
    }

    @Override
    public boolean studentExists(long studentId) {
        return students.containsKey(studentId);
    }

    @Override
    public void deleteStudent(long studentId) {
        students.remove(studentId);
    }

    @Override
    public Collection<Student> getAllStudents() {
        return students.values();
    }

    @Override
    public Student getStudent(long studentId) {
        return students.get(studentId);
    }

    @Override
    public void saveStudent(Student student) {
        if (student == null || student.getId() == null) { throw new NullPointerException("Student must not be null and have Id set."); }
        students.put(student.getId(), student);
    }

    //// END STUDENT MANAGER METHODS ////

    //// START SCHOOL MANAGER METHODS ////


    @Override
    public Collection<School> getAllSchools() {
        return schools.values();
    }

    @Override
    public ErrorCode schoolExists(long schoolId) {
        if(!schools.containsKey(schoolId)) {
            return new ErrorCode(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId }); 
        };
        return ErrorCodes.OK;
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
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
            return new ServiceResponse<Long>(sr.getError(), sr.getErrorParams());
        }
        partialSchool.mergePropertiesIfNull(sr.getValue());
        return replaceSchool(schoolId, partialSchool);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schools.remove(schoolId);
        return new ServiceResponse<Long>((Long) null);
    }

    //// END SCHOOL MANAGER METHODS

    //// BEGIN SCHOOL YEAR MANAGER METHODS

    @Override
    public ServiceResponse<Collection<SchoolYear>> getAllSchoolYears(long schoolId) {
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Collection<SchoolYear>>(code, code.getArguments());
        }
        return new ServiceResponse<Collection<SchoolYear>>(schoolYears.get(schoolId).values());
    }

    @Override
    public ErrorCode schoolYearExists(long schoolId, long schoolYearId) {
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
            return code;
        }
        if(!schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return new ErrorCode(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        return ErrorCodes.OK;
    }

    @Override
    public ServiceResponse<SchoolYear> getSchoolYear(long schoolId, long schoolYearId) {
        ErrorCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<SchoolYear>(code, code.getArguments());
        }
        return new ServiceResponse<SchoolYear>(schoolYears.get(schoolId).get(schoolYearId));
    }

    @Override
    public ServiceResponse<Long> createSchoolYear(long schoolId, SchoolYear schoolYear) {
        ErrorCode code = schoolExists(schoolId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYear.setId(schoolYearId);
        schoolYear.mergePropertiesIfNull(getSchoolYear(schoolId, schoolYearId).getValue());
        return replaceSchoolYear(schoolId, schoolYearId, schoolYear);
    }

    @Override
    public ServiceResponse<Long> deleteSchoolYear(long schoolId, long schoolYearId) {
        ErrorCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        schoolYears.get(schoolId).remove(schoolYearId);
        return new ServiceResponse<Long>((Long) null);
    }
    ////END SCHOOL YEAR MANAGER METHODS
    
    @Override
    public ServiceResponse<Collection<Term>> getAllTerms(long schoolId, long yearId) {
        ErrorCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Collection<Term>>(code, code.getArguments());
        }
        return new ServiceResponse<Collection<Term>>(
                new ArrayList<Term>(schoolYears.get(schoolId).get(yearId).getTerms()));
    }

    @Override
    public ErrorCode termExists(long schoolId, long yearId, long termId) {
        ErrorCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(ErrorCodes.OK)) {
            return code;
        }
        if(schoolYears.containsKey(schoolId) && 
                schoolYears.get(schoolId).containsKey(yearId)) {
            SchoolYear year = schoolYears.get(schoolId).get(yearId);
            Term t = year.findTermById(termId);
            if(null != t) {
                return ErrorCodes.OK;
            }
        }
        return new ErrorCode(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.TERM, termId });
    }

    @Override
    public ServiceResponse<Term> getTerm(long schoolId, long yearId, long termId) {
        ErrorCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Term>(code, code.getArguments());
        }
        SchoolYear year = PersistenceManager.schoolYears.get(schoolId).get(yearId);
        Term t = year.findTermById(termId);
        return new ServiceResponse<Term>(t);
    }

    @Override
    public ServiceResponse<Long> createTerm(long schoolId, long yearId, Term term) {
        ErrorCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = schoolYearExists(schoolId, yearId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        SchoolYear originalYear =  schoolYears.get(schoolId).get(yearId);
        Term termToRemove = originalYear.findTermById(termId);
        originalYear.getTerms().remove(termToRemove);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Section>> getAllSections(long schoolId, long yearId, long termId) {
        ErrorCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Collection<Section>>(code, code.getArguments());
        }
        Collection<Section> returnSections = new ArrayList<Section>();
        if(sections.containsKey(termId)) {
            returnSections = PersistenceManager.sections.get(termId).values();
        }
        return new ServiceResponse<Collection<Section>>(returnSections);
    }

    @Override
    public ErrorCode sectionExists(long schoolId, long yearId, long termId, long sectionId) {
        ErrorCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(ErrorCodes.OK)) {
            return code;
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return new ErrorCode(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sectionId });
        }
        return ErrorCodes.OK;
    }

    @Override
    public ServiceResponse<Section> getSection(long schoolId, long yearId,
            long termId, long sectionId) {
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Section>(code, code.getArguments());
        }
        return new ServiceResponse<Section>(sections.get(termId).get(sectionId));
    }

    @Override
    public ServiceResponse<Long> createSection(long schoolId, long yearId,
            long termId, Section section) {
        ErrorCode code = termExists(schoolId, yearId, termId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        section.setId(sectionId);
        sections.get(termId).put(sectionId, section);
        return new ServiceResponse<Long>(sectionId);
    }

    @Override
    public ServiceResponse<Long> updateSection(long schoolId, long yearId,
            long termId, long sectionId, Section partialSection) {
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Long>(code, code.getArguments());
        }
        sections.get(termId).remove(sectionId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<SectionAssignment>> getAllSectionAssignments(
            long schoolId, long yearId, long termId, long sectionId) {
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<Collection<SectionAssignment>>(code, code.getArguments());
        }
        Collection<SectionAssignment> returnSections = new ArrayList<>();
        if(null != sections.get(termId).get(sectionId).getSectionAssignments()) {
            returnSections = sections.get(termId).get(sectionId).getSectionAssignments();
        }
        return new ServiceResponse<Collection<SectionAssignment>>(returnSections);
    }

    @Override
    public ErrorCode sectionAssignmentExists(long schoolId, long yearId,
            long termId, long sectionId, long sectionAssignmentId) {
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
            return code;
        }
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                null == sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId)) {
            return new ErrorCode(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectionAssignmentId });
        }
        return ErrorCodes.OK;
    }

    @Override
    public ServiceResponse<SectionAssignment> getSectionAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId) {
        ErrorCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(ErrorCodes.OK)) {
            return new ServiceResponse<SectionAssignment>(code, code.getArguments());
        }
        return new ServiceResponse<SectionAssignment>(
                sections.get(termId).get(sectionId).findAssignmentById(sectionAssignmentId));
    }

    @Override
    public ServiceResponse<Long> createSectionAssignment(long schoolId,
            long yearId, long termId, long sectionId,
            SectionAssignment sectionAssignment) {
        ErrorCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(ErrorCodes.OK)) {
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
        ErrorCode code = sectionAssignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.equals(ErrorCodes.OK)) {
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

}
