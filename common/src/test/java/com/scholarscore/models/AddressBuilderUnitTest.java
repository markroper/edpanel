package com.scholarscore.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AddressBuilderUnitTest tests out the Address object's Builder to ensure that constructing an object with the builder
 * Created by cschneider on 10/11/15.
 */
@Test
public class AddressBuilderUnitTest extends AbstractBuilderUnitTest<Address>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Address emptyAddress = new Address();
        Address emptyAddressByBuilder = new Address.AddressBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String state = RandomStringUtils.randomAlphabetic(10);
        String city = RandomStringUtils.randomAlphabetic(10);
        String street = RandomStringUtils.randomNumeric(4) + " " + RandomStringUtils.randomAlphabetic(10);
        String postalCode = RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4);

        Address fullAddress = new Address();
        fullAddress.setId(id);
        fullAddress.setState(state);
        fullAddress.setCity(city);
        fullAddress.setStreet(street);
        fullAddress.setPostalCode(postalCode);

        Address fullAddressByBuilder = new Address.AddressBuilder().
                withState(state).
                withCity(city).
                withStreet(street).
                withPostalCode(postalCode).
                withId(id).
                build();
        return new Object[][]{
                {"Empty address", emptyAddressByBuilder, emptyAddress},
                {"Full address", fullAddressByBuilder, fullAddress}
        };
    }
}
