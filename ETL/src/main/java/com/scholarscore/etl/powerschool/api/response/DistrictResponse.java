package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.District;
import com.scholarscore.etl.powerschool.api.model.Section;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 7/2/15.
 */
public class DistrictResponse {
    public District district;

    @Override
    public String toString() {
        return "DistrictResponse{" +
                "district=" + district +
                '}';
    }

    /**
     * No corresponding ApiModel Object for this object from Powerschool
     *
     * @return
     */
    public Object toInternalModel() {
        return null;
    }
}
