package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.*;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;

import java.util.*;
import java.util.stream.Collectors;

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
    private Map<Long, List<IStaff>> staff;

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
//        result.students = createStudents();
        return result;
    }

    private List<Student> createStudents() {
        StudentResponse response = powerSchool.getDistrictStudents();
        List<Student> addedStudents = response.toInternalModel().stream().map(scholarScore::createStudent).collect(Collectors.toList());
        return addedStudents;
    }

    public Map<Long, List<IStaff>> createStaff() {
        Map<Long, List<IStaff>> staffBySchool = new HashMap<>();
        for (School school : schools) {
            Staffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));

            List<IStaff> apiListOfStaff = response.toInternalModel();


            apiListOfStaff.forEach(staff -> {
                // Create a login for the user
                if (null != staff.getLogin()) {
                    User login = staff.getLogin();
                    // Generate a random password
                    login.setPassword(UUID.randomUUID().toString());
                    try {
                        User result = scholarScore.createUser(login);
                        staff.getLogin().setId(result.getId());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (staff instanceof Teacher) {
                    // TODO: Need to save linkage between staff(teacher/admin) and user identity not persisted currently
                    Teacher teacher = (Teacher)staff;
                    Teacher teacherResponse = scholarScore.createTeacher(teacher);
                    teacher.setId(teacherResponse.getId());
                }
                else if (staff instanceof Administrator) {
                    // TODO: No such entity as administrator yet (needs controller and persistence layer)
                    //Administrator administrator = (Administrator)staff;
                    //apiListOfStaff.add(scholarScore.createAdministrator(administrator));
                }
            });

            staffBySchool.put(school.getId(), apiListOfStaff);
        }
        return staffBySchool;
    }

    public List<School> createSchools() {
        List<School> addedSchools = powerSchool.getSchools().toInternalModel().stream().map(scholarScore::createSchool).collect(Collectors.toList());
        System.out.println("Created " + addedSchools.size() + " schools");
        return addedSchools;
    }
}
