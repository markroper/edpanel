package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.Students;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by mattg on 7/3/15.
 */
public class StudentResponse implements ITranslateCollection<com.scholarscore.models.Student> {
    public Students students;

    @Override
    public Collection<com.scholarscore.models.Student> toInternalModel() {
        com.scholarscore.models.Student out = new com.scholarscore.models.Student();
        out.setId(students.student.id);
        out.setName(students.student.name.toString());

        return Arrays.asList(out);
    }
}
