package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.models.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.Calendar;
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

    protected final AdministratorPersistence adminDao;
    protected final SchoolPersistence schoolDao;
    protected final StudentPersistence studentDao;
    protected final TeacherPersistence teacherDao;
    protected final EntityPersistence<Section> sectionDao;
    protected final EntityPersistence<Course> courseDao;
    protected final EntityPersistence<SchoolYear> schoolYearDao;
    protected final EntityPersistence<Term> termDao;

    public BaseJdbcTest() {

        // Spring context
        ctx = new ClassPathXmlApplicationContext("persistence.xml");

        // Shared DAO objects
        teacherDao = (TeacherPersistence) ctx.getBean("teacherPersistence");
        studentDao = (StudentPersistence) ctx.getBean("studentPersistence");
        adminDao = (AdministratorPersistence) ctx.getBean("administratorPersistence");
        schoolDao = (SchoolPersistence)ctx.getBean("schoolPersistence");
        sectionDao = (EntityPersistence<Section>)ctx.getBean("sectionPersistence");
        courseDao = (EntityPersistence<Course>)ctx.getBean("coursePersistence");
        schoolYearDao = (EntityPersistence<SchoolYear>)ctx.getBean("schoolYearPersistence");
        termDao = (EntityPersistence<Term>)ctx.getBean("termPersistence");

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

        admin.setUsername("mattg");
        admin.setHomePhone("555-1212");
        admin.setName("Matt Greenwood");
        admin.setSourceSystemId("1");
        admin.setHomeAddress(address);

        student.setUsername("mattg");
        student.setName("Matt Greenwood");
        student.setSourceSystemId("1234");
        student.setHomeAddress(address);

        teacher.setUsername("mattg");
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
        Long schoolId = schoolDao.createSchool(school);
        return schoolDao.selectSchool(schoolId);
    }

    public SchoolYear createSchoolYear() {
        School createdSchool = createSchool();
        SchoolYear createSchoolYear = new SchoolYear(schoolYear);
        Long schoolYearId = schoolYearDao.insert(createdSchool.getId(), createSchoolYear);
        return schoolYearDao.select(school.getId(), schoolYearId);
    }

    public Term createTerm() {
        SchoolYear createSchoolYear = createSchoolYear();
        Term createTerm = new Term(term);
        createTerm.setSchoolYear(createSchoolYear);
        Long termId = termDao.insert(createSchoolYear.getId(), createTerm);
        return termDao.select(createSchoolYear.getId(), termId);
    }

    public Course createCourse() {
        Course createCourse = new Course(course);
        createCourse.setSchool(createSchool());
        Long courseId = courseDao.insert(createCourse.getSchool().getId(), createCourse);
        return courseDao.select(createCourse.getSchool().getId(), courseId);
    }
}
