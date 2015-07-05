package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        result.schools = createSchools();
//        result.students = createStudents();
        return result;
    }

    private List<Student> createStudents() {
        StudentResponse response = powerSchool.getDistrictStudents();
        List<Student> addedStudents = new ArrayList<>();
        for (Student student : response.toInternalModel()) {
            addedStudents.add(scholarScore.createStudent(student));
        }
        return addedStudents;
    }

    public List<School> createSchools() {
        SchoolsResponse response = powerSchool.getSchools();
        Collection<School> schools = response.toInternalModel();
        List<School> addedSchools = new ArrayList<>();
        for (School school : schools) {
            addedSchools.add(scholarScore.createSchool(school));
        }
        // add real logging, derp
        System.out.println("Created " + addedSchools.size() + " schools");
        return addedSchools;
    }
}
