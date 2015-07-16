package com.scholarscore.etl.powerschool.api.model;


import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.ListDeserializer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class Staff {
    public static class StaffPhones {
        public String home_phone;

        @Override
        public String toString() {
            return "StaffPhones{" +
                    "home_phone='" + home_phone + '\'' +
                    '}';
        }
    }

    public static class StaffAddresses {
        public static class Home {
            public String street;
            public String city;
            public String state_province;
            public String postal_code;

            @Override
            public String toString() {
                return "Home{" +
                        "street='" + street + '\'' +
                        ", city='" + city + '\'' +
                        ", state_province='" + state_province + '\'' +
                        ", postal_code='" + postal_code + '\'' +
                        '}';
            }
        }

        public Home home;

        @Override
        public String toString() {
            return "StaffAddresses{" +
                    "home=" + home +
                    '}';
        }
    }

    public Long id;
    public String local_id;
    public String admin_username;
    public String teacher_username;
    public Name name;
    public StaffAddresses addresses;
    public StaffPhones phones;

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", local_id='" + local_id + '\'' +
                ", admin_username='" + admin_username + '\'' +
                ", teacher_username='" + teacher_username + '\'' +
                ", name=" + name +
                ", addresses=" + addresses +
                ", phones=" + phones +
                "}\n";
    }
}
