package com.scholarscore.etl.powerschool.api.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scholarscore.etl.powerschool.api.model.School;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public class SchoolsResponse {
    public Schools schools;

    @Override
    public String toString() {
        return "SchoolsResponse{" +
                "schools=" + schools +
                '}';
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
