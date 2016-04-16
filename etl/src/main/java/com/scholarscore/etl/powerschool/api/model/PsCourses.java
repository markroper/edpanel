package com.scholarscore.etl.powerschool.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.CoursesDeserializer;
import com.scholarscore.etl.ITranslateCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = CoursesDeserializer.class)
public class PsCourses extends ArrayList<PsCourse> implements ITranslateCollection<com.scholarscore.models.Course>  {

    @Override
    public Collection<com.scholarscore.models.Course> toInternalModel() {
        List<com.scholarscore.models.Course> response = new ArrayList<>();
        this.forEach(course -> {
            com.scholarscore.models.Course apiModel = new com.scholarscore.models.Course();
            apiModel.setName(course.course_name);
            apiModel.setNumber(course.course_number);
            if (null != course.id) {
                apiModel.setSourceSystemId(course.id.toString());
            }
            response.add(apiModel);
        });
        return response;
    }
}
