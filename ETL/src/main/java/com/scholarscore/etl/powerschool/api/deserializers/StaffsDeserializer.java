package com.scholarscore.etl.powerschool.api.deserializers;

import com.scholarscore.etl.powerschool.api.model.PsStaff;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;

/**
 * Created by mattg on 7/15/15.
 */
public class StaffsDeserializer extends ListDeserializer<PsStaffs, PsStaff> {
    @Override
    String getEntityName() {
        return "staff";
    }
}
