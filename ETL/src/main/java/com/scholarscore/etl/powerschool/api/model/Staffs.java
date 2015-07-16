package com.scholarscore.etl.powerschool.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.StaffsDeserializer;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = StaffsDeserializer.class)
public class Staffs extends ArrayList<Staff> implements ITranslateCollection<User> {

    @Override
    public Collection<User> toInternalModel() {
        return null;
    }
}
