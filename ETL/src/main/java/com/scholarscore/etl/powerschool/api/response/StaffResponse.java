package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
@XmlRootElement(name = "staffs")
public class StaffResponse {

    public Staffs staffs;

    @Override
    public String toString() {
        return "StaffResponse{" +
                "staff=" + staffs +
                '}';
    }
}
