package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Behavior;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jwinch on 7/23/15.
 */
public class BehaviorResponse implements ITranslateCollection<com.scholarscore.models.Behavior> {

    Integer rowcount;
    private List<Behavior> data = new ArrayList<>();

    @Override
    public Collection<com.scholarscore.models.Behavior> toInternalModel() {
        ArrayList<com.scholarscore.models.Behavior> toReturn = new ArrayList<>();

        for (Behavior behavior : data) {
            com.scholarscore.models.Behavior out = new com.scholarscore.models.Behavior();
            out.setRemoteStudentId(behavior.DLSAID);
            out.setName(behavior.Behavior);
            out.setBehaviorCategory(behavior.BehaviorCategory);
            out.setBehaviorDate(behavior.BehaviorDate);
            out.setPointValue(behavior.PointValue);

            // mostly-empty student with just student name (it's all we have)
            com.scholarscore.models.Student student = new com.scholarscore.models.Student();
            student.setName(getStudentName(behavior));
            out.setStudent(student);
            // mostly-empty teacher with just teacher name (it's all we have)
            com.scholarscore.models.Teacher teacher = new com.scholarscore.models.Teacher();
            teacher.setName(getStaffName(behavior));
            out.setTeacher(teacher);

            out.setRoster(behavior.Roster);
            toReturn.add(out);
        }
        return toReturn;
    }
    
    private String getStudentName(Behavior behavior) { 
        return (StringUtils.isEmpty(behavior.StudentFirstName) ? "" : behavior.StudentFirstName + " ")
                + (StringUtils.isEmpty(behavior.StudentMiddleName) ? "" : behavior.StudentMiddleName + " ")
                + (StringUtils.isEmpty(behavior.StudentLastName) ? "" : behavior.StudentLastName).trim();
    }
    
    private String getStaffName(Behavior behavior) { 
        return
                (StringUtils.isEmpty(behavior.StaffFirstName) ? "" : behavior.StaffFirstName + " ")
                + (StringUtils.isEmpty(behavior.StaffMiddleName) ? "" : behavior.StaffMiddleName + " ")
                + (StringUtils.isEmpty(behavior.StaffLastName) ? "" : behavior.StaffLastName).trim();
        
    }
}
