package com.scholarscore.etl.powerschool.api.response;


import com.scholarscore.etl.ITranslateCollection;
import com.scholarscore.etl.powerschool.api.model.PsSchool;
import com.scholarscore.models.School;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public class PsSchoolsResponse implements ITranslateCollection<School> {
    public PsSchools schools;

    @Override
    public String toString() {
        return "SchoolsResponse{" +
                "schools=" + schools +
                '}';
    }

    @Override
    public Collection<com.scholarscore.models.School> toInternalModel() {
        ArrayList<com.scholarscore.models.School> response = new ArrayList<>();
        for (PsSchool school : schools.school) {
            com.scholarscore.models.School apiSchool = new com.scholarscore.models.School();
            apiSchool.setName(school.name);
            if(null != school.school_number) {
                apiSchool.setNumber(Long.valueOf(school.school_number));
            }
            if (null != school.id) {
                apiSchool.setSourceSystemId(school.id.toString());
            }
            response.add(apiSchool);
        }
        return response;
    }

    public class PsSchools {

        public List<PsSchool> school = new ArrayList<>();
        public String expansions;

        public void setSchool(List<PsSchool> school) {
            this.school = school;
        }

        @Override
        public String toString() {
            return school.toString();
        }

    }

}
