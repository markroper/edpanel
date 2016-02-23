package com.scholarscore.models.query.dimension;

import com.scholarscore.models.query.Dimension;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/22/16
 * Time: 10:39 PM
 */
@Test(groups = { "unit" })
public class DimensionUnitTest {

    @Test
    public void testDimensionHasClass() { 
        for (Dimension dimension : Dimension.values()) {
            // this method throws an exception if it can't find the class...
            IDimension dimensionClass = Dimension.buildDimension(dimension);
            // but just to be safe, assert null here anyways.
            assertNotNull(dimensionClass);
        }
    }
    
}
