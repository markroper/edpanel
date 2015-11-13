package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.PsCourse;
import com.scholarscore.etl.powerschool.api.model.PsCourses;

/**
 * Created by mattg on 8/5/15.
 */
public class CoursesDeserializer extends ListDeserializer<PsCourses, PsCourse> {
    @Override
    String getEntityName() {
        return "course";
    }
}
