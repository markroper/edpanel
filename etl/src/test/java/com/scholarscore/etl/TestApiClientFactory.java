package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * User: jordan
 * Date: 3/31/16
 * Time: 2:07 PM
 */
public class TestApiClientFactory {

    private static Long userCounter = 0L;
    private static Long schoolCounter = 0L;
    private static Long schoolYearCounter = 0L;
    private static Long termCounter = 0L;
    private static Long sectionCounter = 0L;
    private static Long courseCounter = 0L;
    private static Long sectionAssignmentCounter = 0L;
    private static Long attendanceCounter = 0L;
    
    private static HashMap<Long, School> schools = new HashMap<>();
    private static HashMap<Long, SchoolYear> schoolYears = new HashMap<>();
    private static HashMap<Long, Staff> staff = new HashMap<>();
    private static HashMap<Long, Student> students = new HashMap<>();
    private static HashMap<Long, Term> terms = new HashMap<>();
    // courseId -> course
    private static HashMap<Long, Course> courseIdToCourse = new HashMap<>();
    // schoolId -> courses
    private static HashMap<Long, HashSet<Course>> schoolIdToCourses = new HashMap<>();
    // sectionId -> section
    private static HashMap<Long, Section> sections = new HashMap<>();
    // schoolId -> sections
    private static HashMap<Long, HashSet<Section>> schoolIdToSections = new HashMap<>();
    // sectionId -> studentSectionGrades
    private static HashMap<Long, HashSet<StudentSectionGrade>> sectionIdToStudentSectionGrades = new HashMap<>();
    private static HashSet<Gpa> gpas = new HashSet<>();
    private static HashMap<Long, Assignment> sectionAssignments = new HashMap<>();
    private static HashMap<Long, HashMap<Long, StudentAssignment>> studentAssignments = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static IAPIClient buildTestApiClient() throws HttpClientException {
        IAPIClient testApiClient = Mockito.mock(IAPIClient.class);
        
        // getSchools()
        when(testApiClient.getSchools()).thenReturn(schools.values());

        // getCourses()
        when(testApiClient.getCourses(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            HashSet<Course> courses = schoolIdToCourses.get(schoolId);
            if (courses == null) { courses = new HashSet<>(); }
            return courses;
        });

        // getGpas()
        when(testApiClient.getGpas()).thenAnswer(invocation -> {
            return new ArrayList(gpas);
        });
        
        // getSections()
        when (testApiClient.getSections(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            HashSet<Section> schoolSections = schoolIdToSections.get(schoolId);
            if (schoolSections == null) { schoolSections = new HashSet<>(); }
            return schoolSections;
        });
        
        // edPanel.getStudentSectionGrades(
        // school.getId(),
        //        createdSection.getTerm().getSchoolYear().getId(),
        //        createdSection.getTerm().getId(),
        //        createdSection.getId());
        when(testApiClient.getStudentSectionGrades(anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("TermId cannot be null!");
            }
            if (sectionId == null) {
                throw new RuntimeException("SectionId cannot be null!");
            }

            HashSet<StudentSectionGrade> ssgs = sectionIdToStudentSectionGrades.get(sectionId);
            if (ssgs == null) { ssgs = new HashSet<>(); } 
            return ssgs;
        });

        when(testApiClient.getStudentAssignments(anyLong(), anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            Long assignmentId = (Long) args[4];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("TermId cannot be null!");
            }
            if (sectionId == null) {
                throw new RuntimeException("SectionId cannot be null!");
            }
            if (assignmentId == null) {
                throw new RuntimeException("AssignmentId cannot be null!");
            }

            // TODO ETL Tests - capture and return student assignments
            return new ArrayList<StudentAssignment>();
        });

        //edPanel.getSectionAssignments(
        //        school.getId(),
        //        createdSection.getTerm().getSchoolYear().getId(),
        //        createdSection.getTerm().getId(),
        //        createdSection.getId());
        when(testApiClient.getSectionAssignments(anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            // TODO ETL Tests - capture and return section assignments
            return new ArrayList<Assignment>();
        });
        
        // createSchool(School school)
        when(testApiClient.createSchool(any(School.class))).thenAnswer(invocation -> {
//            Object mock = invocation.getMock();
            Object[] args = invocation.getArguments();
            School school = (School) args[0];
            if (school == null) { throw new RuntimeException("School cannot be null!"); }

            if (school.getId() == null) {
                school.setId(schoolCounter++);
            }
            schools.put(school.getId(), school);
            return school;
        });
        
        when(testApiClient.getSchoolDays(anyLong())).thenAnswer(invocation -> {
            // TODO ETL Tests - capture and return school days
           return new ArrayList<SchoolDay>(); 
        });
        
        // createSection
        when(testApiClient.createSection(anyLong(), anyLong(), anyLong(), any(Section.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Section section = (Section) args[3];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("Term cannot be null!");
            }
            if (section == null) {
                throw new RuntimeException("Section cannot be null!");
            }
            
            if (section.getId() == null) {
                section.setId(sectionCounter++);
            }
            sections.put(section.getId(), section);

            HashSet<Section> sections = schoolIdToSections.get(schoolId);
            if (sections == null) {
                sections = new HashSet<>();
            }
            sections.add(section);
            schoolIdToSections.put(schoolId, sections);
            return section;
        });

        // getSchoolYears(Long schoolId)
        when(testApiClient.getSchoolYears(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) { throw new RuntimeException("SchoolId cannot be null!"); }

            School school = schools.get(schoolId);
            List<SchoolYear> schoolYears = null;
            if (school != null) {
                schoolYears = school.getYears();
            }
            if (schoolYears == null) { schoolYears = new ArrayList<>(); }
            return schoolYears;
        });
        
        // createSchoolYear(Long schoolId, SchoolYear schoolYear);
        when(testApiClient.createSchoolYear(anyLong(), any(SchoolYear.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            SchoolYear schoolYear = (SchoolYear) args[1];
            if (schoolId == null) { throw new RuntimeException("SchoolId cannot be null!"); }
            if (schoolYear == null) { throw new RuntimeException("SchoolYear cannot be null!"); }
            
            if (schoolYear.getId() == null) {
                schoolYear.setId(schoolYearCounter++);
            }
            schoolYears.put(schoolYear.getId(), schoolYear);
            return schoolYear;
        });

        // createAdministrator
        when(testApiClient.createAdministrator(any(Staff.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Staff staffToCreate = (Staff) args[0];
            if (staffToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (staffToCreate.getId() == null) {
                staffToCreate.setId(userCounter++);
            }
            staff.put(staffToCreate.getId(), staffToCreate);
            return staffToCreate;
        });

        // createTeacher
        when(testApiClient.createTeacher(any(Staff.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Staff staffToCreate = (Staff) args[0];
            if (staffToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (staffToCreate.getId() == null) {
                staffToCreate.setId(userCounter++);
            }
            staff.put(staffToCreate.getId(), staffToCreate);
            return staffToCreate;
        });

        // createStudent
        when(testApiClient.createStudent(any(Student.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Student studentToCreate = (Student) args[0];
            if (studentToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (studentToCreate.getId() == null) {
                studentToCreate.setId(userCounter++);
            }
            students.put(studentToCreate.getId(), studentToCreate);
            return studentToCreate;
        });

        // createTerm(school.getId(), sourceTerm.getSchoolYear().getId(), sourceTerm);
        when(testApiClient.createTerm(anyLong(), anyLong(), any(Term.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Term term = (Term) args[2];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (term == null) {
                throw new RuntimeException("Term cannot be null!");
            }

            if (term.getId() == null) {
                term.setId(termCounter++);
            }
            terms.put(term.getId(), term);
            return term;
        });
        
        // createCourse
        when(testApiClient.createCourse(anyLong(), any(Course.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Course course = (Course) args[1];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (course == null) {
                throw new RuntimeException("Course cannot be null!");
            }
            if (course.getId() == null) {
                course.setId(courseCounter++);
            }
            courseIdToCourse.put(course.getId(), course);
  
            HashSet<Course> courses = schoolIdToCourses.get(schoolId);
            if (courses == null) { courses = new HashSet<>(); }
            courses.add(course);
            schoolIdToCourses.put(schoolId, courses);
            return course;
        });
        
        //createAssignment
        when(testApiClient.createSectionAssignment(anyLong(), anyLong(), anyLong(), anyLong(), any(Assignment.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            Assignment assignment = (Assignment) args[4];
            if (schoolId == null) { throw new RuntimeException("SchoolId cannot be null!"); }
            if (schoolYearId == null) { throw new RuntimeException("SchoolYearId cannot be null!"); }
            if (termId == null) { throw new RuntimeException("Term cannot be null!"); }
            if (sectionId == null) { throw new RuntimeException("Section cannot be null!"); }
            if (assignment == null) { throw new RuntimeException("Assignment cannot be null!"); }
            
            if (assignment.getId() == null) {
                assignment.setId(sectionAssignmentCounter++);
            }
            sectionAssignments.put(assignment.getId(), assignment);
            return assignment;
        });
        
        //createAttendances
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long studentId = (Long) args[1];
            List<Attendance> attendances = (List<Attendance>) args[2];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (studentId == null) {
                throw new RuntimeException("StudentId cannot be null!");
            }

            attendances.stream().filter(attendance -> attendance.getId() == null).forEach(attendance -> {
                attendance.setId(attendanceCounter++);
            });

            return attendances;
        }).when(testApiClient).createAttendances(anyLong(), anyLong(), anyListOf(Attendance.class));
        
        when(testApiClient.getAttendance(anyLong(), anyLong())).thenAnswer(invocation -> {
            // TODO ETL: support attendance
            return new ArrayList<Attendance>();
        });
        
        when(testApiClient.getSchools()).thenAnswer(invocation -> {
            // TODO ETL: support school storage
            return new ArrayList<School>();
        });
        
        return testApiClient;
    }
}
