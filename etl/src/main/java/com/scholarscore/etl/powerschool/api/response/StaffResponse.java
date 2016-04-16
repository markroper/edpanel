package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.ITranslateCollection;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.models.user.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

/**
 * Ideally there will be a PsStaff type object in our API that extends from User from
 * which we can differentiate a Teacher, from a PsStaff from a PsStudent from a User (whomever that is?)
 *
 * Created by mattg on 7/2/15.
 */
@XmlRootElement(name = "staffs")
public class StaffResponse implements ITranslateCollection<User> {

    public PsStaffs staffs;

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
