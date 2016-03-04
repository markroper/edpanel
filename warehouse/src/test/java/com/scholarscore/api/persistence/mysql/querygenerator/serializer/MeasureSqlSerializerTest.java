package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.AttendanceSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.BaseAttendanceSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior.BehaviorSqlSerializer;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * User: jordan
 * Date: 2/18/16
 * Time: 1:01 PM
 */
@Test(groups = { "unit" })
public class MeasureSqlSerializerTest {
    
    @Test
    public void testMeasureHasSerializer() { 
        for (Measure measure : Measure.values()) {
            MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(measure);
            assertNotNull(mss, "Measure " + measure + " is defined in Measures Enum but produces a NULL MeasureSqlSerializer!");
        }
    }
    
    @Test
    public void testMeasureSerializerFromClause() {
        for (Measure measure : Measure.values()) {
            MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(measure);
            assertNotNull(mss, "Measure " + measure + " is defined in Measures Enum but produces a NULL MeasureSqlSerializer!");
            String fromClause = mss.toFromClause();
            assertNotNull(fromClause, "Measure " + measure + " returned a null fromClause from its respective MeasureSqlSerializer");
            
            // FROM clause *must* end with a space!
            assertTrue(fromClause.length() > 0, "Measure " + measure + " expected to have FROM clause with length greater than 0");

            String lastCharacter = fromClause.substring(fromClause.length() - 1, fromClause.length());
            assertTrue(" ".equals(lastCharacter), "Deserializer " + mss.getClass().getSimpleName() + " (for measure " + measure + ") did not return a from Clause with a trailing space.\n From Clause: '" + fromClause + "'");
        }
    }

    @Test
    public void testMeasureSerializerJoinClause() {
        for (Measure measure : Measure.values()) {
            MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(measure);
            
            Dimension testDimension = pickJoinDimension(mss);
            String joinClause = mss.toJoinClause(testDimension);
            assertNotNull(joinClause, "Expected join clause not to be null from serializer " + mss.getClass().getSimpleName());
        }
    }
    
    private Dimension pickJoinDimension(MeasureSqlSerializer mss) {
        Dimension validDimension = null;

        if (mss instanceof BaseAttendanceSqlSerializer) { 
            // these serializers only work with certain dimensions 
            return Dimension.SCHOOL;
        }
        
        Dimension firstTable = mss.toTableDimension();
        Dimension secondTable = mss.toSecondTableDimension();
        for (Dimension trialDimension : Dimension.values()) {
            if (firstTable != null && firstTable.equals(trialDimension)) { continue; }
            if (secondTable != null && secondTable.equals(trialDimension)) { continue; } 
            validDimension = trialDimension;
            break;
        }
        return validDimension;
    }

}
