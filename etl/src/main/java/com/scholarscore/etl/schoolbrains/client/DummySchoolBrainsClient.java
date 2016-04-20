package com.scholarscore.etl.schoolbrains.client;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import java.util.List;
import java.util.Map;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 5:41 PM
 */
public class DummySchoolBrainsClient implements ISchoolBrainsClient {
    @Override
    public List<School> getSchools() throws HttpClientException {
        
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<Course> getCourses() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<Student> getStudents() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<SchoolYear> getSchoolYears() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<Section> getSections() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public Map<String, List<String>> getSectionEnrollment() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<Gpa> getGpas() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }

    @Override
    public List<Staff> getStaff() throws HttpClientException {
        throw new RuntimeException("not implemented in dummy client!");
    }
}
