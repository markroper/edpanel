package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class Addresses {
    public static class Physical {
        public String street;
        public String city;
        public String state_province;
        public String postal_code;

        @Override
        public String toString() {
            return "Physical{" +
                    "street='" + street + '\'' +
                    ", city='" + city + '\'' +
                    ", stateProvince='" + state_province + '\'' +
                    ", postalCode='" + postal_code + '\'' +
                    '}';
        }
    }

    public Physical physical;

    @Override
    public String toString() {
        return "Addresses{" +
                "physical=" + physical +
                '}';
    }
}
