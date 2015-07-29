package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Student;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response to deanslist 'get students' endpoint
 * 
 * Created by jwinch on 7/23/15.
 */
public class StudentResponse implements ITranslateCollection<com.scholarscore.models.Student> {
    
    Integer rowcount;
    private List<Student> data = new ArrayList<>();
    
    @Override
    public Collection<com.scholarscore.models.Student> toInternalModel() {
        // TODO: convert to scholarscore student model
        ArrayList<com.scholarscore.models.Student> toReturn = new ArrayList<>();
        for (Student student : data) {
            com.scholarscore.models.Student out = new com.scholarscore.models.Student();
            String name = 
                      (student.FirstName == null? "" : student.FirstName)
                    + (student.MiddleName == null ? "" : " " + student.MiddleName)
                    + (student.LastName == null ? "" : " " + student.LastName)
                              .trim();
            out.setName(name);
            
            out.setSourceSystemId(student.DLSchoolID);
            // TODO: the above could also be set to...
//            out.setSourceSystemId(student.StudentSchoolID);
//            out.setSourceSystemId(student.SecondaryStudentID);

            // TODO: have school name here but need school ID to match with model
            // out.setCurrentSchoolId(student.SchoolName);

            toReturn.add(out);
        }
        return toReturn;
    }
    
    @Override
    public String toString() { 
        return "StudentResponse: { \"rowcount\": \"" + rowcount + "\", \"data\": [" + data.toString() + "]";
    }
}
