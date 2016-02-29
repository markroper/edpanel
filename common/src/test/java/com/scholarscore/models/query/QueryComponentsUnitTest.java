package com.scholarscore.models.query;

import com.scholarscore.models.query.dimension.IDimension;
import com.scholarscore.models.query.measure.IMeasure;
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
    
    @Test
    public void testQueryComponentsContainsAllDimensions() {
        Set<IDimension> availableDimensions = queryComponents.getAvailableDimensions();
        for (Dimension dimension : Dimension.values()) {
            IDimension builtDimension = Dimension.buildDimension(dimension);
            boolean dimensionFoundInSet = availableDimensions.contains(builtDimension);
            assertTrue(dimensionFoundInSet, "Query Components Available Dimensions does not define dimension " + dimension.getClass().getSimpleName() + "\n"
            + "Are you sure the class " + builtDimension.getClass().getSimpleName() + " extends BaseDimension or otherwise overrides equals() and hashcode()?"
            );
        }
    }
    
    @Test
    public void testQueryComponentsContainsAllMeasures() {
        Set<IMeasure> availableMeasures = queryComponents.getAvailableMeasures();
        for (Measure measure : Measure.values()) {
            IMeasure builtMeasure = Measure.buildMeasure(measure);
            boolean measureFoundInSet = availableMeasures.contains(builtMeasure);
            assertTrue(measureFoundInSet, "Query Components Available Measures does not define measure " + builtMeasure.getClass().getSimpleName() + "\n"
                            + "Are you sure the class " + builtMeasure.getClass().getSimpleName() + " extends BaseMeasure or otherwise overrides equals() and hashcode()?"
            );
        }
    }
    
}
