package com.scholarscore.etl.powerschool.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.StaffsDeserializer;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.Address;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = StaffsDeserializer.class)
public class PsStaffs extends ArrayList<PsStaff> implements ITranslateCollection<User> {

    @Override
    public List<User> toInternalModel() {
        List<User> collection = new ArrayList<>();
        for (PsStaff staff : this) {

            // MJG: Should we have a notion of an administrator?
            Person entity;
            if (staff.isAdmin()) {
                entity = new Administrator();
            } else {
                entity = new Teacher();
            }
            
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

            entity.setName(staff.name.toString());
            if (null != staff.id) {
                entity.setSourceSystemId(staff.id.toString());
            }

            // Define the login user so we can create that as well via the API
            entity.setEnabled(true);
            entity.setSourceSystemUserId(staff.local_id.toString());
            entity.setUsername(staff.getUsername());
            collection.add(entity);
        }
        return collection;
    }
}