package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = { "functional" })
public class BaseJdbcTest {
    protected final Address address = new Address();
    protected final School school = new School();
    protected final Student student = new Student();
    protected final Teacher teacher = new Teacher();
    protected final Section section = new Section();
    protected final Course course = new Course();
    protected final SchoolYear schoolYear = new SchoolYear();
    protected final Term term = new Term();

    protected final Administrator admin = new Administrator();
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
    protected final StudentSectionGradePersistence studentSectionGradeDao;

    private School createdSchool;
    private SchoolYear createdSchoolYear;
    private Term createdTerm;
    private Course createdCourse;
    private Section createdSection;
    private User createdUser;
    private Student createdStudent;
    private StudentSectionGrade createdStudentSectionGrade;
    private Teacher createdTeacher;

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
        // Shared domain model objects
        address.setStreet("51 Round Hill Rd.");
        address.setPostalCode("02364");
        address.setCity("Kingston");
        address.setState("MA");

        school.setName("School Test 1");
        school.setAddress(address);
        school.setPrincipalName("Principal Name");
        school.setPrincipalEmail("principal@school.com");
        school.setSourceSystemId("1");
        school.setMainPhone("555-555-1212");

        User adminUser = new User();
        adminUser.setUsername("mattg");
        admin.setUser(adminUser);
        admin.setHomePhone("555-1212");
        admin.setName("Matt Greenwood");
        admin.setSourceSystemId("1");
        admin.setHomeAddress(address);

        User studentUser = new User();
        studentUser.setUsername("mattg");
        student.setUser(studentUser);
        student.setName("Matt Greenwood");
        student.setSourceSystemId("1234");
        student.setHomeAddress(address);

        User teacherUser = new User();
        teacherUser.setUsername("mattg");
        teacher.setUser(teacherUser);
        teacher.setHomePhone("555-1212");
        teacher.setSourceSystemId("abc");
        teacher.setHomeAddress(address);

        course.setSchool(school);
        course.setSourceSystemId("1");
        course.setNumber("course1");
        course.setName("course_name");

        section.setName("section1");
        section.setRoom("room1");
        section.setStartDate(new Date());
        section.setEndDate(new Date());
        section.setCourse(course);

        schoolYear.setName("2015");
        schoolYear.setStartDate(new Date());
        schoolYear.setEndDate(new Date());
        schoolYear.setSchool(school);

        term.setName("term1");
        term.setStartDate(new Date());
        term.setEndDate(new Date());
        term.setSchoolYear(schoolYear);
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
            createdSection.setStartDate(new Date());
            createdSection.setCourse(createCourse());
            Long id = sectionDao.insert(createdSection.getTerm().getId(),
                    createdSection);
            createdSection.setId(id);
        }
        return createdSection;
    }

    public User createUser() {
        if (null == createdUser) {
            User user = new User();
            user.setUsername("foobar" + System.currentTimeMillis());
            user.setName("testName");
            user.setPassword("testPassword");
            user.setEnabled(true);
            userDao.createUser(user);
            createdUser = user;
        }
        return createdUser;
    }

    public Student createStudent() {
        if (null == createdStudent) {
            createdStudent = new Student(student);
            createdStudent.setUser(createUser());
            Address homeAddress = new Address(address);
            Address mailingAddress = new Address(address);
            homeAddress.setId(null);
            mailingAddress.setId(null);
            createdStudent.setHomeAddress(homeAddress);
            createdStudent.setMailingAddress(mailingAddress);

            Long id = studentDao.createStudent(createdStudent);
            createdStudent = studentDao.select(id);
        }
        return createdStudent;
    }

    public StudentSectionGrade createStudentSectionGrade() {
        if (null == createdStudentSectionGrade) {
            createdStudentSectionGrade = new StudentSectionGrade();
            createdStudentSectionGrade.setSection(createSection());
            createdStudentSectionGrade.setComplete(true);
            createdStudentSectionGrade.setGrade(5d);
            createdStudentSectionGrade.setStudent(createStudent());
            studentSectionGradeDao.insert(createdStudentSectionGrade.getSection().getId(), createdStudentSectionGrade.getStudent().getId(), createdStudentSectionGrade);
        }
        return createdStudentSectionGrade;
    }

    public Teacher createTeacher() {
        if (null == createdTeacher) {
            Long id = teacherDao.createTeacher(teacher);
            createdTeacher = teacherDao.select(id);
        }
        return createdTeacher;
    }
}
