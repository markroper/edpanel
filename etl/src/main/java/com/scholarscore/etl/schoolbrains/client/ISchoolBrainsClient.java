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
 * Created by markroper on 4/15/16.
 */
public interface ISchoolBrainsClient {

    List<School> getSchools() throws HttpClientException;

    List<Course> getCourses() throws HttpClientException;

    List<Student> getStudents() throws HttpClientException;

    List<SchoolYear> getSchoolYears() throws HttpClientException;

    List<Section> getSections() throws HttpClientException;

    Map<String, List<String>> getSectionEnrollment() throws HttpClientException;

    List<Gpa> getGpas() throws HttpClientException;

    List<Staff> getStaff() throws HttpClientException;
}
