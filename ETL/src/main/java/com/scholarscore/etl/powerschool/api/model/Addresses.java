package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class Addresses {
    public class Physical {
        public String street;
        public String city;
        public String stateProvince;
        public String postalCode;

        @Override
        public String toString() {
            return "Physical{" +
                    "street='" + street + '\'' +
                    ", city='" + city + '\'' +
                    ", stateProvince='" + stateProvince + '\'' +
                    ", postalCode='" + postalCode + '\'' +
                    '}';
        }
    }

    Physical physical;

    @Override
    public String toString() {
        return "Addresses{" +
                "physical=" + physical +
                '}';
    }
}
