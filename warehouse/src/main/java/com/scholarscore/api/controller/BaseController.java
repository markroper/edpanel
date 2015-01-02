package com.scholarscore.api.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.persistence.SchoolManager;
import com.scholarscore.api.persistence.SchoolYearManager;
import com.scholarscore.api.persistence.StudentManager;
import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.api.util.ErrorResponseFactory;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.SubjectArea;

/**
 * All SpringMVC controllers defined in the package subclass this base
 * controller class, which contains utility methods used to generate error
 * and non-error API responses.
 * 
 * @author markroper
 *
 */
@Validated
public abstract class BaseController implements StudentManager, SchoolManager, SchoolYearManager {
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

    // Todo refactor_persistence: everything from here to 75 should be private.
    //Student structure: Map<studentId, Student>
    private final AtomicLong studentCounter = new AtomicLong();
    private static Map<Long, Student> students = Collections.synchronizedMap(new HashMap<Long, Student>());
    //Student section grade structure: Map<studentId, Map<sectionId, StudentSectionGrade>>
    protected final AtomicLong studentSectGradeCounter = new AtomicLong();
    protected static Map<Long, Map<Long, Map<Long, StudentSectionGrade>>> studentSectionGrades =
            Collections.synchronizedMap(new HashMap<Long, Map<Long, Map<Long, StudentSectionGrade>>>());

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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof ErrorCode) {
            //If the object passed in is an error code, localize the error message to build the response
            ErrorCode err = (ErrorCode) obj;
            ErrorResponseFactory factory = new ErrorResponseFactory();
            return new ResponseEntity(factory.localizeError(err), err.getHttpStatus());
        } else if(obj instanceof ServiceResponse){
            //If the object is a ServiceResponse, resolve whether to return the ErrorCode or the value instance member
            ServiceResponse sr = (ServiceResponse) obj;
            if(null != sr.getValue()) {
                if(sr.getValue() instanceof Long) {
                    //For a long, return it as an EntityId so that serializaiton is of the form { id: <longval> }
                    return new ResponseEntity(new EntityId((Long)sr.getValue()), HttpStatus.OK);
                } else {
                    //For all other cases, just return the value
                    return new ResponseEntity(sr.getValue(), HttpStatus.OK);
                }
            } else if(null != sr.getError()){
                //Handle the error code on the service response
                return respond(sr.getError(), sr.getErrorParams());
            } else {
                //If both value and error code are null on the service response, we're dealing with a successful body-less response
                return new ResponseEntity((Object) null, HttpStatus.OK);
            }
        } 
        //If the object is neither a ServiceResponse nor an ErrorCode, respond with it directly
        return new ResponseEntity(obj, HttpStatus.OK);
    }
    
    protected ResponseEntity<ErrorCode> respond(ErrorCode code, Object[] args) {
        ErrorResponseFactory factory = new ErrorResponseFactory();
        ErrorCode returnError = new ErrorCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<ErrorCode>(factory.localizeError(returnError), returnError.getHttpStatus());
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
    public ServiceResponse<School> getSchool(long schoolId) {
        if(!schoolExists(schoolId)) {
            return new ServiceResponse<School>(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
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
        if(null == schools.get(schoolId)) {
            return new ServiceResponse<Long>(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
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
        if(!schoolExists(schoolId)) {
            return new ServiceResponse<Long>(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        schools.remove(schoolId);
        return new ServiceResponse<Long>((Long) null);
    }

    //// END SCHOOL MANAGER METHODS

    //// BEGIN SCHOOL YEAR MANAGER METHODS

    @Override
    public Collection<SchoolYear> getAllSchoolYears(long schoolId) {
        if (!schoolExists(schoolId)) {
            // TODO Jordan: throw an exception instead? (applies to next 5 methods as well)
            return null;
        }
        return schoolYears.get(schoolId).values();
    }

    @Override
    public boolean schoolYearExists(long schoolId, long schoolYearId) {
        if (!schoolExists(schoolId)) {
            return false;
        }
        return schoolYears.get(schoolId).containsKey(schoolYearId);
    }

    @Override
    public SchoolYear getSchoolYear(long schoolId, long schoolYearId) {
        if (!schoolExists(schoolId)) {
            return null;
        }
        return schoolYears.get(schoolId).get(schoolYearId);
    }

    @Override
    public long createSchoolYear(long schoolId, SchoolYear schoolYear) {
        if (!schoolExists(schoolId)) {
            return -1;
        }
        schoolYear.setId(schoolYearCounter.incrementAndGet());
        schoolYears.get(schoolId).put(schoolYear.getId(), schoolYear);
        return schoolYear.getId();
    }

    @Override
    public void saveSchoolYear(long schoolId, SchoolYear schoolYear) {
        if (!schoolExists(schoolId)) {
            return;
        }
        if (schoolYear == null || schoolYear.getId() == null) { throw new NullPointerException("Object must not be null and have Id set."); }
        schoolYears.get(schoolId).put(schoolYear.getId(), schoolYear);
    }

    @Override
    public void deleteSchoolYear(long schoolId, long schoolYearId) {
        if (!schoolExists(schoolId)) {
            return;
        }
        schoolYears.get(schoolId).remove(schoolYearId);
    }

//// END SCHOOL YEAR MANAGER METHODS

}
