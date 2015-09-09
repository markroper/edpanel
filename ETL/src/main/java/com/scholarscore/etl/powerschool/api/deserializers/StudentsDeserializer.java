package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;
import com.scholarscore.etl.powerschool.api.model.Student;
import com.scholarscore.etl.powerschool.api.model.Students;

/**
 * Created by mattg on 8/3/15.
 */
public class StudentsDeserializer extends ListDeserializer<Students, Student> {
    @Override
    String getEntityName() {
        return "student";
    }
}
