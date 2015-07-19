package com.scholarscore.etl.powerschool.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.StaffsDeserializer;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.Address;
import com.scholarscore.models.Teacher;
import com.scholarscore.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = StaffsDeserializer.class)
public class Staffs extends ArrayList<Staff> implements ITranslateCollection<Teacher> {

    @Override
    public Collection<Teacher> toInternalModel() {
        List<Teacher> teachers = new ArrayList<>();
        for (Staff staff : this) {
            Teacher teacher = new Teacher();
            teacher.setName(staff.name.toString());
            if (null != staff.id) {
                teacher.setSourceSystemId(staff.id.toString());
            }

            // Define the login user so we can create that as well via the API
            User user = new User();
            user.setEnabled(true);
            if (null != staff.teacher_username) {
                user.setUsername(staff.teacher_username);
            }
            else if (null != staff.admin_username) {
                user.setUsername(staff.admin_username);
            }
            user.setName(staff.name.toString());
            teacher.setLogin(user);
            if (null != staff.phones && null != staff.phones.home_phone) {
                teacher.setHomePhone(staff.phones.home_phone);
            }
            if (null != staff.addresses && null != staff.addresses.home) {
                Address homeAddress = new Address();
                homeAddress.setCity(staff.addresses.home.city);
                homeAddress.setPostalCode(staff.addresses.home.postal_code);
                homeAddress.setState(staff.addresses.home.state_province);
                homeAddress.setStreet(staff.addresses.home.street);
                teacher.setHomeAddress(homeAddress);
            }
            teachers.add(teacher);
        }
        return teachers;
    }
}
