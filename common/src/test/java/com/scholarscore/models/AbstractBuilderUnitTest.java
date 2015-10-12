package com.scholarscore.models;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The AbstractBuilderUnitTest provides the equality test that we expect for each of the domain model objects.
 * It dictates that each implementing class provides the data provider with a given name "builderProvider"
 * Created by cschneider on 10/11/15.
 */
public abstract class AbstractBuilderUnitTest<T> {

    /**
     * Each builderProvider should provide test cases for empty and full objects in the for of
     * {[string msg describing what is happening], <T> [object made with builder], <T>[object made with setter]}
     * @return a two dimensional array of test cases
     */
    @DataProvider
    public abstract Object[][] builderProvider();

    @Test(dataProvider = "builderProvider")
    public void testAttendanceBuilder(final String msg, T objectByBuilder, T objectBySetter){
        Assert.assertEquals(objectByBuilder, objectBySetter, "Unexpected inequality");
    }
}
