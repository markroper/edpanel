package com.scholarscore.models.query.measure;

import com.scholarscore.models.query.Measure;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/22/16
 * Time: 10:37 PM
 */
@Test(groups = { "unit" })
public class MeasureUnitTest {

    @Test
    public void testMeasureHasClass() {
        for (Measure measure : Measure.values()) {
            IMeasure measureClass = measure.buildMeasure();
            assertNotNull(measureClass, "Measure " + measure + " is defined in Measures Enum but Measure.buildMeasure produces null!");
        }
    }

}
