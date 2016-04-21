package com.scholarscore.etl.schoolbrains.client;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.schoolbrains.parser.BaseParser;
import com.scholarscore.etl.schoolbrains.parser.CourseParser;
import com.scholarscore.etl.schoolbrains.parser.SchoolParser;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 5:41 PM
 */
public class DummySchoolBrainsClient implements ISchoolBrainsClient {
    
    private <T> List<T> getEntities(BaseParser<T> baseParser, String resourceName) throws HttpClientException {
        URL resource = getClass().getClassLoader().getResource("schoolbrains/" + resourceName + ".csv");
        if (resource == null) {
            throw new RuntimeException("Cannot load resource schoolbrains/" + resourceName + ".csv");
        }
        File input = new File(resource.getFile());
        Set<T> parsedEntities = baseParser.parse(input);
        if (parsedEntities == null) {
            throw new RuntimeException("getEntities returned null set.");
        }
        if (parsedEntities.size() <= 0) {
            throw new RuntimeException("Parsed 0 entities, which seems like a failure.");
        }
        return new ArrayList<>(parsedEntities);
    }
    
    @Override
    public List<School> getSchools() throws HttpClientException {
        return getEntities(new SchoolParser(), "EdPanelSchools");
    }

    @Override
    public List<Course> getCourses() throws HttpClientException {
//        return getEntities(new CourseParser(), "");
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
