package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.PrepScore;
import com.scholarscore.models.user.Student;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: jordan
 * Date: 12/21/14
 * Time: 5:33 PM
 *
 * Classes that implement this interface are able to manage persistence of Student records.
 */
public interface StudentManager {

    public ServiceResponse<Collection<Student>> getAllStudents(Long schoolId);

    public StatusCode studentExists(long studentId);

    public ServiceResponse<Student> getStudent(long studentId);

    public ServiceResponse<Student> getStudentBySourceSystemId(Long ssid);

    // Creates a student and populates the Id field (if ID is specified it will be ignored)
    public ServiceResponse<Long> createStudent(Student student);

    // replace the existing student record with this one (ID will be taken from the student)
    public ServiceResponse<Long> replaceStudent(long studentId, Student student);
    
    public ServiceResponse<Long> updateStudent(long studentId, Student student);

    public ServiceResponse<Long> deleteStudent(long studentId);
    
    public ServiceResponse<List<PrepScore>> getStudentPrepScore(Long studentIds[], Date startDate, Date endDate);

}
