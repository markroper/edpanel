package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.student.PsStudent;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;

/**
 * Created by mattg on 8/3/15.
 */
public class StudentsDeserializer extends ListDeserializer<PsStudents, PsStudent> {
    @Override
    String getEntityName() {
        return "student";
    }
}
