package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;

/**
 * Created by mattg on 7/15/15.
 */
public class StaffsDeserializer extends ListDeserializer<Staffs, Staff> {
    @Override
    String getEntityName() {
        return "staff";
    }
}
