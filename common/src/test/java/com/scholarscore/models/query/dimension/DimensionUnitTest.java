package com.scholarscore.models.query.dimension;

import com.scholarscore.models.query.Dimension;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
    
    public void testDimensionHasOrdering() {
        for (Dimension dimension : Dimension.values()) {
            HashSet<Dimension> dimensionSet = new HashSet<>();
            dimensionSet.add(dimension);
            List<Dimension> dimensionList = Dimension.resolveOrderedDimensions(dimensionSet);
            assertNotNull(dimensionList, "Dimension.resolveOrderedDimensions returned null dimension list "
                    + "for set containing only dimension " + dimension);
            assertTrue(dimensionList.size() > 0, "Dimension " + dimension + " not returned from Dimension.resolveOrderedDimensions");
        }
    }
    
}
