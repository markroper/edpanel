package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.GoalPersistence;
import com.scholarscore.api.persistence.SchoolPersistence;
import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Address;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.goal.AssignmentGoal;
import com.scholarscore.models.goal.BehaviorGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = { "functional" })
public class BaseJdbcTest {
    protected final Address address = new Address();
    protected final School school = new School();
    protected final Student student = new Student();
    protected final Staff teacher = new Staff();
    protected final Section section = new Section();
    protected final Course course = new Course();
    protected final SchoolYear schoolYear = new SchoolYear();
    protected final Term term = new Term();
    protected final BehaviorGoal behaviorGoal = new BehaviorGoal();
    protected final AssignmentGoal assignmentGoal = new AssignmentGoal();
    protected final StudentAssignment studentAssignment = new StudentAssignment();
    protected final GradedAssignment gradedAssignment = new GradedAssignment();

    protected final Staff admin = new Staff();
    protected final ApplicationContext ctx;

    protected final UserPersistence userDao;
    protected final AdministratorPersistence adminDao;
    protected final SchoolPersistence schoolDao;
    protected final StudentPersistence studentDao;
    protected final TeacherPersistence teacherDao;
    protected final EntityPersistence<Section> sectionDao;
    protected final EntityPersistence<Course> courseDao;
    protected final EntityPersistence<SchoolYear> schoolYearDao;
    protected final EntityPersistence<Term> termDao;
    protected final EntityPersistence<Assignment> assignmentDao;
    protected final StudentAssignmentPersistence studentAssignmentDao;
    protected final StudentSectionGradePersistence studentSectionGradeDao;
    protected final GoalPersistence goalDao;

    protected final HibernateTemplate hibernateTemplate;
    
    private School createdSchool;
    private SchoolYear createdSchoolYear;
    private Term createdTerm;
    private Course createdCourse;
    private Section createdSection;
    private User createdUser;
    private Student createdStudent;
    private StudentAssignment createdStudentAssignment;
    private StudentSectionGrade createdStudentSectionGrade;
    private Staff createdTeacher;
    private Staff createdAdmin;
    private BehaviorGoal createdBehaviorGoal;
    private AssignmentGoal createdAssignmentGoal;
    private Assignment createdGradedAssignment;

    String adminName = "Random McDudeFace";
    String adminUsername = "rmcdudeface";

    String teacherName = "Random McTeacherFace";
    String teacherUsername = "rmcteacherface";

    String studentName = "Random McStudentFace";
    String studentUsername = "rmcstudentface";
    
    
    public BaseJdbcTest() {

        // Spring context
        ctx = new ClassPathXmlApplicationContext("persistence.xml");

        // Shared DAO objects
        userDao = (UserPersistence)ctx.getBean("userPersistence");
        teacherDao = (TeacherPersistence) ctx.getBean("teacherPersistence");
        studentDao = (StudentPersistence) ctx.getBean("studentPersistence");
        adminDao = (AdministratorPersistence) ctx.getBean("administratorPersistence");
        schoolDao = (SchoolPersistence)ctx.getBean("schoolPersistence");
        sectionDao = (EntityPersistence<Section>)ctx.getBean("sectionPersistence");
        courseDao = (EntityPersistence<Course>)ctx.getBean("coursePersistence");
        schoolYearDao = (EntityPersistence<SchoolYear>)ctx.getBean("schoolYearPersistence");
        termDao = (EntityPersistence<Term>)ctx.getBean("termPersistence");
        assignmentDao = (EntityPersistence<Assignment>)ctx.getBean("assignmentPersistence");
        studentSectionGradeDao = (StudentSectionGradePersistence)ctx.getBean("studentSectionGradePersistence");
        goalDao = (GoalPersistence)ctx.getBean("goalPersistence");
        studentAssignmentDao = (StudentAssignmentPersistence)ctx.getBean("studentAssignmentPersistence");
        
        hibernateTemplate = (HibernateTemplate) ctx.getBean("hibernateTemplate");

        // Shared domain model objects
        address.setStreet("51 Round Hill Rd.");
        address.setPostalCode("02364");
        address.setCity("Kingston");
        address.setState("MA");

        school.setName("School Test 1");
        school.setAddress(address);
        school.setPrincipalName("Principal Name");
        school.setPrincipalEmail("principal@school.com");
        school.setMainPhone("555-555-1212");
        
        Staff adminUser = new Staff();
        adminUser.setUsername(adminUsername);
        adminUser.setIsAdmin(true);
        admin.setHomePhone("555-1212");
        admin.setName(adminName);
        admin.setHomeAddress(address);
        admin.setIsAdmin(true);

        Student studentUser = new Student();
        student.setUsername(studentUsername);
        student.setName(studentName);
        student.setSourceSystemId("1234");
        student.setHomeAddress(address);

        Staff teacherUser = new Staff();
        teacher.setUsername(teacherUsername);
        teacher.setName(teacherName);
        teacher.setHomePhone("555-1212");
        teacher.setSourceSystemId("abc");
        teacher.setHomeAddress(address);

        course.setSchool(school);
        course.setSourceSystemId("1");
        course.setNumber("course1");
        course.setName("course_name");

        section.setName("section1");
        section.setRoom("room1");
        section.setStartDate(LocalDate.now());
        section.setEndDate(LocalDate.now());
        section.setCourse(course);

        schoolYear.setName("2015");
        schoolYear.setStartDate(LocalDate.now());
        schoolYear.setEndDate(LocalDate.now());
        schoolYear.setSchool(school);

        term.setName("term1");
        term.setStartDate(LocalDate.now());
        term.setEndDate(LocalDate.now());
        term.setSchoolYear(schoolYear);

        gradedAssignment.setName("Graded Assignment");
        gradedAssignment.setType(AssignmentType.CLASSWORK);
        
        behaviorGoal.setStaff(teacher);
        behaviorGoal.setStudent(student);
        behaviorGoal.setDesiredValue(5d);
        behaviorGoal.setName("Does this behave as expected");
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(LocalDate.now());
        behaviorGoal.setEndDate(LocalDate.now());

        assignmentGoal.setApproved(LocalDate.now());
        assignmentGoal.setStaff(teacher);
        assignmentGoal.setStudent(student);
        assignmentGoal.setDesiredValue(5d);
        assignmentGoal.setName("Does this behave as expected");
        assignmentGoal.setStudentAssignment(studentAssignment);
    }

    public School createSchool() {
        if (null == createdSchool) {
            Long schoolId = schoolDao.createSchool(school);
            this.createdSchool = schoolDao.selectSchool(schoolId);
        }
        return createdSchool;
    }

    public SchoolYear createSchoolYear() {
        if (null == createdSchoolYear) {
            School createdSchool = createSchool();
            createdSchoolYear = new SchoolYear(schoolYear);
            createdSchoolYear.setSchool(createdSchool);
            Long schoolYearId = schoolYearDao.insert(createdSchool.getId(), createdSchoolYear);
            createdSchoolYear.setId(schoolYearId);
        }
        return createdSchoolYear;
    }

    public Term createTerm() {
        if (null == createdTerm) {
            SchoolYear createSchoolYear = createSchoolYear();
            createdTerm = new Term(term);
            createdTerm.setSchoolYear(createSchoolYear);
            Long termId = termDao.insert(createSchoolYear.getId(), createdTerm);
            createdTerm.setId(termId);
        }
        return createdTerm;
    }

    public Course createCourse() {
        if (null == createdCourse) {
            createdCourse = new Course(course);
            createdCourse.setSchool(createSchool());
            Long courseId = courseDao.insert(createdCourse.getSchool().getId(), createdCourse);
            createdCourse.setId(courseId);
        }
        return createdCourse;
    }

    public Section createSection() {
        if (null == createdSection) {
            createdSection = new Section();
            createdSection.setTerm(createTerm());
            createdSection.setName("Section1");
            createdSection.setRoom("section_room_1");
            createdSection.setTerm(createTerm());
            createdSection.setStartDate(LocalDate.now());
            createdSection.setCourse(createCourse());
            Long id = sectionDao.insert(createdSection.getTerm().getId(),
                    createdSection);
            createdSection.setId(id);
        }
        return createdSection;
    }

    public User createUser() {
        if (null == createdUser) {
            Staff user = new Staff();
            user.setUsername("foobar" + System.currentTimeMillis());
            user.setPassword("testPassword");
            user.setEnabled(true);
            Long userId = userDao.createUser(user);
            createdUser = userDao.selectUser(userId);
        }
        return createdUser;
    }

    public Student createStudent() {
        if (null == createdStudent) {
            createdStudent = new Student(student);
            Address homeAddress = new Address(address);
            Address mailingAddress = new Address(address);
            homeAddress.setId(null);
            mailingAddress.setId(null);
            createdStudent.setHomeAddress(homeAddress);
            createdStudent.setMailingAddress(mailingAddress);

            Long id = studentDao.createStudent(createdStudent);
            createdStudent = studentDao.select(id);
            createdStudent.setId(id);
        }
        return createdStudent;
    }
    
    public Assignment createAssignment() { 
        if (null == createdGradedAssignment) {
            createdGradedAssignment = new GradedAssignment(gradedAssignment);

            // must have section, sectionID, and we don't know if these are created yet
            Section section = createSection();
            createdGradedAssignment.setSection(section);
            createdGradedAssignment.setSectionFK(section.getId());
            
            Long assignmentId = assignmentDao.insert(createdGradedAssignment.getSectionFK(), createdGradedAssignment);
            createdGradedAssignment = assignmentDao.select(createdGradedAssignment.getSectionFK(), assignmentId);
        }
        return createdGradedAssignment;
    }
    
    public StudentAssignment createStudentAssignment() { 
        if (null == createdStudentAssignment) {
            createdStudentAssignment = new StudentAssignment(studentAssignment);
            createdStudentAssignment.setAssignment(createAssignment());

            // must have saved student (i.e. with studentID) in order to save this one
            Student student = createStudent();
            createdStudentAssignment.setStudent(student);

            studentAssignmentDao.insert(createdStudentAssignment.getAssignment().getId(), createdStudentAssignment);
        }
        return createdStudentAssignment;
    }

    public StudentSectionGrade createStudentSectionGrade() {
        if (null == createdStudentSectionGrade) {
            createdStudentSectionGrade = new StudentSectionGrade();
            Section createdSection = createSection();
            createdStudentSectionGrade.setSection(createdSection);
            Student createdStudent = createStudent();
            createdStudentSectionGrade.setStudent(createdStudent);
            createdStudentSectionGrade.setComplete(true);
            SectionGrade sg = new SectionGrade();
            sg.setDate(LocalDate.now());
            sg.setScore(5D);
            sg.setSectionFk(createdSection.getId());
            sg.setStudentFk(createdStudent.getId());
            createdStudentSectionGrade.setOverallGrade(sg);
            createdStudentSectionGrade.setStudent(createStudent());
            studentSectionGradeDao.insert(createdStudentSectionGrade.getSection().getId(), createdStudentSectionGrade.getStudent().getId(), createdStudentSectionGrade);
        }
        return createdStudentSectionGrade;
    }

    public Staff createTeacher() {
        if (null == createdTeacher) {
            Long id = teacherDao.createTeacher(teacher);
            createdTeacher = teacherDao.select(id);
            createdTeacher.setId(id);
        }

        return createdTeacher;
    }
    
    public Staff createAdmin() { 
        if (null == createdAdmin) {
            Long id = adminDao.createAdministrator(admin);
            createdAdmin = adminDao.select(id);
            createdAdmin.setId(id);
        }
        return createdAdmin;
    }

    public Goal createBehaviorGoal() {
        if (null == createdBehaviorGoal) {
            createdBehaviorGoal = new BehaviorGoal();
            createdBehaviorGoal.setName("Behaves nicely when created");
            createdBehaviorGoal.setEndDate(LocalDate.now());
            createdBehaviorGoal.setStartDate(LocalDate.now());
            createdBehaviorGoal.setStudent(createStudent());
            createdBehaviorGoal.setStaff(createTeacher());
            createdBehaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
            createdBehaviorGoal.setDesiredValue(5d);
        }
        return createdBehaviorGoal;
    }

    public Goal createAssignmentGoal() {
        if (null == createdBehaviorGoal) {
            createdAssignmentGoal = new AssignmentGoal();
            createdAssignmentGoal.setName("Behaves nicely when created");
            createdAssignmentGoal.setStudent(createStudent());
            createdAssignmentGoal.setStaff(createTeacher());
            
            if (null == createdStudentAssignment) {
                createdStudentAssignment = createStudentAssignment();
            }
            
            createdAssignmentGoal.setStudentAssignment(createdStudentAssignment);
            createdAssignmentGoal.setApproved(LocalDate.now());
            createdAssignmentGoal.setDesiredValue(5d);
        }
        return createdAssignmentGoal;
    }
    
    @BeforeMethod
    public void initialize() {
        // many tests rely on this user not existing so they can create it -- so delete it if it exists
        
        User existingAdminUser = userDao.selectUserByName(adminUsername);
        if (existingAdminUser != null) {
            userDao.deleteUser(existingAdminUser.getId());
            // createdAdmin = null (I guess we never use this admin?)
        }
        User existingTeacher = userDao.selectUserByName(teacherUsername);
        if (existingTeacher != null) {
            userDao.deleteUser(existingTeacher.getId());
            createdTeacher = null;
        }
        User existingStudent = userDao.selectUserByName(studentUsername);
        if (existingStudent != null) {
            userDao.deleteUser(existingStudent.getId());
            createdStudent = null;
        }
        
    }

}
