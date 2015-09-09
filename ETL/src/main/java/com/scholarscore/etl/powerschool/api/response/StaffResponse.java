package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.Staffs;
import com.scholarscore.models.Student;
import com.scholarscore.models.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

/**
 * Ideally there will be a Staff type object in our API that extends from User from
 * which we can differentiate a Teacher, from a Staff from a Student from a User (whomever that is?)
 *
 * Created by mattg on 7/2/15.
 */
@XmlRootElement(name = "staffs")
public class StaffResponse implements ITranslateCollection<com.scholarscore.models.User> {

    public Staffs staffs;

    @Override
    public String toString() {
        return "StaffResponse{" +
                "staffs=" + staffs +
                '}';
    }

    @Override
    public Collection<User> toInternalModel() {
        return null;
    }
}
