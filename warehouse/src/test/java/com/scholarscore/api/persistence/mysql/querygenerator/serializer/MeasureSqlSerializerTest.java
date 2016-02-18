package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.measure.IMeasure;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/18/16
 * Time: 1:01 PM
 */
@Test(groups = { "unit" })
public class MeasureSqlSerializerTest {

    // this first test requires nothing in warehouse and could be moved to the common package
    public void testMeasureHasClass() {
        for (Measure measure : Measure.values()) {
            IMeasure measureClass = Measure.buildMeasure(measure);
            assertNotNull(measureClass, "Measure " + measure + " is defined in Measures Enum but Measure.buildMeasure produces null!");
        }
    }
    
    @Test
    public void testMeasureHasSerializer() { 
        for (Measure measure : Measure.values()) {
            MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(measure);
            assertNotNull(mss, "Measure " + measure + " is defined in Measures Enum but produces a NULL MeasureSqlSerializer!");
        }
    }
    
}
