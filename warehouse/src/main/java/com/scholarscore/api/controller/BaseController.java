package com.scholarscore.api.controller;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.scholarscore.api.controller.api.SchoolManager;
import com.scholarscore.api.controller.api.StudentManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorResponseFactory;
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

/**
 * All SpringMVC controllers defined in the package subclass this base
 * controller class, which contains utility methods used to generate error
 * and non-error API responses.
 * 
 * @author markroper
 *
 */
@Validated
public abstract class BaseController implements StudentManager, SchoolManager {
    //TODO: @mroper we need to add a real persistence layer that we call instead of manipulating this map
    public static final String JSON_ACCEPT_HEADER = "application/json";
    
    protected static final String SCHOOL = "school";
    protected static final String ASSIGNMENT = "assignment";
    protected static final String COURSE = "course";
    protected static final String SCHOOL_YEAR = "school year";
    protected static final String TERM = "term";
    protected static final String SECTION = "section";
    protected static final String SECTION_ASSIGNMENT = "section assignment";
    protected static final String STUDENT_ASSIGNMENT = "student assignment";
    protected static final String STUDENT = "student";
    protected static final String STUDENT_SECTION_GRADE = "student section grade";

    // Todo Jordan: everything from here to 75 should be private.
    //Student structure: Map<studentId, Student>
    private final AtomicLong studentCounter = new AtomicLong();
    private static Map<Long, Student> students = Collections.synchronizedMap(new HashMap<Long, Student>());
    //Student section grade structure: Map<studentId, Map<sectionId, StudentSectionGrade>>
    protected static Map<Long, Map<Long, StudentSectionGrade>> studentSectionGrades = 
            Collections.synchronizedMap(new HashMap<Long, Map<Long, StudentSectionGrade>>());
    
    //School structure: Map<schoolId, School>
    private final AtomicLong schoolCounter = new AtomicLong();
    private static Map<Long, School> schools = Collections.synchronizedMap(new HashMap<Long, School>());
    //School year structure: Map<SchoolId, Map<SchoolYearId, SchoolYear>> note: schoolYears contain terms
    protected final AtomicLong schoolYearCounter = new AtomicLong();
    protected final AtomicLong termCounter = new AtomicLong();
    protected static Map<Long, Map<Long, SchoolYear>> schoolYears = Collections.synchronizedMap(new HashMap<Long, Map<Long, SchoolYear>>());
    //Map<termId, Map<sectionId, Section>>
    protected final static AtomicLong sectionCounter = new AtomicLong();
    protected final static Map<Long, Map<Long, Section>> sections = Collections.synchronizedMap(new HashMap<Long, Map<Long, Section>>());

    //Map<SectionId, Map<sectionAssignmentId, SectionAssignment>>
    protected final AtomicLong sectionAssignmentCounter = new AtomicLong();
    //Map<sectionAssignmentId, Map<studentAssignmentId, StudentAssignment>>
    protected final AtomicLong studentAssignmentCounter = new AtomicLong();
    protected static Map<Long, Map<Long, StudentAssignment>> studentAssignments = 
            Collections.synchronizedMap(new HashMap<Long, Map<Long, StudentAssignment>>());
    //Subject area structure Map<SchoolId, Map<subjectAreaId, SubjectArea>>
    protected final static AtomicLong subjectAreaCounter = new AtomicLong();
    protected final static Map<Long, Map<Long, SubjectArea>> subjectAreas = Collections.synchronizedMap(new HashMap<Long, Map<Long, SubjectArea>>());
    //Course structure: Map<schoolId, Map<courseId, Course>>
    protected final static AtomicLong courseCounter = new AtomicLong();
    protected final static Map<Long, Map<Long, Course>> courses = Collections.synchronizedMap(new HashMap<Long, Map<Long, Course>>());
    //Assignments structure: Map<courseId, Map<assignmentId, Assignment>>
    protected final static AtomicLong assignmentCounter = new AtomicLong();
    protected final static Map<Long, Map<Long, Assignment>> assignments = Collections.synchronizedMap(new HashMap<Long, Map<Long, Assignment>>());
    
    @SuppressWarnings("unchecked")
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof ErrorCode) {
            ErrorCode err = (ErrorCode) obj;
            ErrorResponseFactory factory = new ErrorResponseFactory();
            return new ResponseEntity(factory.localizeError(err), err.getHttpStatus());
        } else {
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }
    
    protected ResponseEntity<ErrorCode> respond(ErrorCode code, Object[] args) {
        ErrorResponseFactory factory = new ErrorResponseFactory();
        ErrorCode returnError = new ErrorCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<ErrorCode>(factory.localizeError(returnError), returnError.getHttpStatus());
    }
    
    protected HashSet<Long> resolveTermIds(SchoolYear year) {
        HashSet<Long> termIds = new HashSet<>();
        if(null != year.getTerms()) {
            for(Term t : year.getTerms()) {
                termIds.add(t.getId());
            }
        }
        return termIds;
    }
    
    protected Term getTermById(Set<Term> terms, Long termId) {
        Term termWithTermId = null;
        if(null != terms) {
            for(Term t : terms) {
                if(t.getId().equals(termId)) {
                    termWithTermId = t;
                    break;
                }
            }
        }
        return termWithTermId;
    }

    //// STUDENT MANAGER METHODS ////

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
    public boolean schoolExists(long schoolId) {
        return schools.containsKey(schoolId);
    }

    @Override
    public School getSchool(long schoolId) {
        return schools.get(schoolId);
    }

    @Override
    public long createSchool(School school) {
        school.setId(schoolCounter.incrementAndGet());
        schools.put(school.getId(), school);
        return school.getId();
    }

    @Override
    public void saveSchool(School school) {
        if (school == null || school.getId() == null) { throw new NullPointerException("School must not be null and have Id set."); }
        schools.put(school.getId(), school);
    }

    @Override
    public void deleteSchool(long schoolId) {
        schools.remove(schoolId);
    }

    //// END SCHOOL MANAGER METHODS

}
