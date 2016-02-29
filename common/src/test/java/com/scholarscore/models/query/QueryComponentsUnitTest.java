package com.scholarscore.models.query;

import com.scholarscore.models.query.dimension.IDimension;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertTrue;

/**
 * User: jordan
 * Date: 2/29/16
 * Time: 10:46 AM
 */
@Test(groups = {"unit"})
public class QueryComponentsUnitTest {

    final QueryComponents queryComponents = new QueryComponents();
    
    /*
    @Test
    public void testQueryComponentsContainsAllDimensions() {
        Set<IDimension> availableDimensions = queryComponents.getAvailableDimensions();
        for (Dimension dimension : Dimension.values()) {
            IDimension builtDimension = Dimension.buildDimension(dimension);
            assertTrue(availableDimensions.contains(builtDimension), "Query Components Available Dimensions does not define dimension " + dimension);
        }
    }
    */
    
    @Test
    public void testQueryComponentsContainsAllMeasures() { 
    
    }
    
}
