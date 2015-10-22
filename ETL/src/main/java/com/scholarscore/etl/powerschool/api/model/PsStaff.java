package com.scholarscore.etl.powerschool.api.model;


/**
 * Defines a PsStaff Member which can be a teacher, or administrator.  We can determine which
 * type of user we have based on the admin_username or teacher_username properties
 *
 * Created by mattg on 6/28/15.
 */
public class PsStaff {

    public Long id;
    public String local_id;
    public String admin_username;
    public String teacher_username;
    public PsName name;
    public StaffAddresses addresses;
    public StaffPhones phones;

    public boolean isAdmin() {
        return admin_username != null;
    }

    public boolean isTeacher() {
        return teacher_username != null;
    }

    public String getUsername() {
        if (null != admin_username) {
            return admin_username;
        }
        if (null != teacher_username) {
            return teacher_username;
        }
        return null;
    }

    @Override
    public String toString() {
        return "PsStaff{" +
                "id=" + id +
                ", local_id='" + local_id + '\'' +
                ", admin_username='" + admin_username + '\'' +
                ", teacher_username='" + teacher_username + '\'' +
                ", name=" + name +
                ", addresses=" + addresses +
                ", phones=" + phones +
                "}\n";
    }

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

}
