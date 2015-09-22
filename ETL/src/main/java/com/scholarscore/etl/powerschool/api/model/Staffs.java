package com.scholarscore.etl.powerschool.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.StaffsDeserializer;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = StaffsDeserializer.class)
public class Staffs extends ArrayList<Staff> implements ITranslateCollection<IStaff> {

    @Override
    public List<IStaff> toInternalModel() {
        List<IStaff> collection = new ArrayList<>();
        for (Staff staff : this) {

            // MJG: Should we have a notion of an administrator?
            IStaff entity;
            if (staff.isAdmin()) {
                entity = new Administrator();
            }
            else {
                entity = new Teacher();
            }

            entity.setName(staff.name.toString());
            if (null != staff.id) {
                entity.setSourceSystemId(staff.id.toString());
            }

            // Define the login user so we can create that as well via the API
            User user = new User();
            user.setEnabled(true);
            user.setUsername(staff.getUsername());

            // Create linkage between user and teacher/admin
            user.setName(staff.name.toString());

            // We set this so that we can also create the user it doesn't get sent
            // with the original request
            entity.setUser(user);

            if (null != staff.phones && null != staff.phones.home_phone) {
                entity.setHomePhone(staff.phones.home_phone);
            }
            if (null != staff.addresses && null != staff.addresses.home) {
                Address homeAddress = new Address();
                homeAddress.setCity(staff.addresses.home.city);
                homeAddress.setPostalCode(staff.addresses.home.postal_code);
                homeAddress.setState(staff.addresses.home.state_province);
                homeAddress.setStreet(staff.addresses.home.street);
                entity.setHomeAddress(homeAddress);
            }
            collection.add(entity);
        }
        return collection;
    }
}
