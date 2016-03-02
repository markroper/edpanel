package com.scholarscore.api.persistence;

import com.scholarscore.models.query.Dimension;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/29/16
 * Time: 9:44 AM
 */
@Test(groups = { "unit" })
public class DbMappingsUnitTest {
    
    @Test
    public void testDimensionToTableNameContainsAllExistentDimensions() { 
        for (Dimension dimension : Dimension.values()) {
            String tableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimension);
            assertNotNull(tableName, "Expected Dimension " + dimension + " to produce non-null String result from DbMappings.DIMENSION_TO_TABLE_NAME");
        }
    }
    
    @Test
    public void testTableNameToDimensionContainsAllReturnedDimensionNames() {
        for (Dimension dimension : Dimension.values()) {
            String tableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimension);
            assertNotNull(tableName, "Expected Dimension " + dimension + " to produce non-null String result from DbMappings.DIMENSION_TO_TABLE_NAME");
            Dimension recoveredDimension = DbMappings.TABLE_NAME_TO_DIMENSION.get(tableName);
            assertNotNull(recoveredDimension, "Expected String " + tableName + " (gotten in response to dimension " + dimension + ") " + 
                    "not to be null in DbMappings.DIMENSION_TO_TABLE_NAME");
        }
    }
    
}
