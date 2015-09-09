package com.scholarscore.etl.powerschool.api.response;


import com.scholarscore.etl.powerschool.api.model.School;
import com.scholarscore.models.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public class SchoolsResponse implements ITranslateCollection<com.scholarscore.models.School> {
    public Schools schools;

    @Override
    public String toString() {
        return "SchoolsResponse{" +
                "schools=" + schools +
                '}';
    }

    @Override
    public Collection<com.scholarscore.models.School> toInternalModel() {
        ArrayList<com.scholarscore.models.School> response = new ArrayList<>();
        for (School school : schools.school) {
            com.scholarscore.models.School apiSchool = new com.scholarscore.models.School();
            apiSchool.setName(school.name);
            if (null != school.id) {
                apiSchool.setSourceSystemId(school.id.toString());
            }
            response.add(apiSchool);
        }
        return response;
    }

    public class Schools {

        public List<School> school = new ArrayList<>();
        public String expansions;

        public void setSchool(List<School> school) {
            this.school = school;
        }

        @Override
        public String toString() {
            return school.toString();
        }

    }

}
