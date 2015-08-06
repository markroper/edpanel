package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.Course;
import com.scholarscore.etl.powerschool.api.model.Courses;
import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;

/**
 * Created by mattg on 8/5/15.
 */
public class CoursesDeserializer extends ListDeserializer<Courses, Course> {
    @Override
    String getEntityName() {
        return "course";
    }
}
