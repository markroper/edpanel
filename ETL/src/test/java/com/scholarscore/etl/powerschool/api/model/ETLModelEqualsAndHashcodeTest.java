package com.scholarscore.etl.powerschool.api.model;

import com.scholarscore.models.ModelEqualsAndHashcodeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * User: jordan
 * Date: 9/27/15
 * Time: 8:54 PM
 */
@Test(groups = { "unit" })
public class ETLModelEqualsAndHashcodeTest extends ModelEqualsAndHashcodeTest {

    @Override
    public String getPackageToScan() {
        return "com.scholarscore.etl.powerschool.api.model";
    }

    @Override
    public Set<String> getExcludedClassNames() {
        return new HashSet<String>() {{
            // if you want to exclude a model class from this test, add it here. e.g...
//            add(packageToScan + ".ModelEqualsAndHashcodeTest");
        }};
    }
}
