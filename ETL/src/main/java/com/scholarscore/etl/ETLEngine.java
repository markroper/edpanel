package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.*;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.*;

/**
 * This is the E2E flow for powerschool import to scholarScore export - we have references to both clients and
 * can invoke get API's from powerschool and POST (create) API's from scholarScore.  We assume for now that we'll seek
 * out entities from the scholarscore database before inserting them into the database rather than assuming a trash
 * and burn strategy.
 *
 * Created by mattg on 7/3/15.
 */
public class ETLEngine implements IETLEngine {

    private IPowerSchoolClient powerSchool;
    private IAPIClient scholarScore;
    private List<School> schools;

    // Collections by schoolId
    private Map<Long, List<Course>> courses;
    private Map<Long, List<User>> staff;
    private Map<Long, List<Student>> students;

    public void setPowerSchool(IPowerSchoolClient powerSchool) {
        this.powerSchool = powerSchool;
    }

    public IPowerSchoolClient getPowerSchool() {
        return powerSchool;
    }

    public void setScholarScore(IAPIClient scholarScore) {
        this.scholarScore = scholarScore;
    }

    public IAPIClient getScholarScore() {
        return scholarScore;
    }

    @Override
    public MigrationResult migrateDistrict() {
        MigrationResult result = new MigrationResult();
        this.schools = createSchools();
        this.staff = createStaff();
        this.students = createStudents();
        this.courses = createCourses();
        return result;
    }

    private Map<Long, List<Course>> createCourses() {

        Map<Long, List<Course>> result = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            Courses response = powerSchool.getCoursesBySchool(schoolId);
            Collection<Course> apiListOfCourses = response.toInternalModel();

            apiListOfCourses.forEach(course -> {
                scholarScore.createCourse(school.getId(), course);
            });
        }
        return result;
    }

    private Map<Long, List<Student>> createStudents() {
        Map<Long, List<Student>> studentsBySchool = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            Students response = powerSchool.getStudentsBySchool(schoolId);
            Collection<Student> apiListOfStudents = response.toInternalModel();

            List<Student> students = new ArrayList<>();
            apiListOfStudents.forEach(student -> {
                student.setCurrentSchoolId(school.getId());
                Student createdStudent = scholarScore.createStudent(student);
                student.setId(createdStudent.getId());
                students.add(student);
            });
            studentsBySchool.put(schoolId, students);
        }
        return studentsBySchool;
    }

    /**
     * Create the user entry along side the teacher and administrator entries
     * @return
     */
    public Map<Long, List<User>> createStaff() {
        Map<Long, List<User>> staffBySchool = new HashMap<>();
        for (School school : schools) {
            Staffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));

            List<User> apiListOfStaff = response.toInternalModel();


            apiListOfStaff.forEach(staff -> {
                staff.setPassword(UUID.randomUUID().toString());
                try {
                    User result = scholarScore.createUser(staff);
                    staff.setId(result.getId());
                }
                catch (Exception e) {
                }
            });

            staffBySchool.put(school.getId(), apiListOfStaff);
        }
        return staffBySchool;
    }

    public List<School> createSchools() {
        SchoolsResponse powerSchools = powerSchool.getSchools();
        List<School> schools = (List<School>) powerSchools.toInternalModel();
        for (School school : schools) {
            School response = scholarScore.createSchool(school);
            school.setId(response.getId());
        }
        return schools;
    }
}
