package com.scholarscore.etl.state.ma;

import com.scholarscore.models.state.ma.McasResult;
import com.scholarscore.util.McasParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
@Test(groups = {"unit"})
public class McasParserUnitTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("MCASTEST.csv").getFile());
        McasParser parser = new McasParser(input);
        List<McasResult> results = parser.parse();
        Assert.assertEquals(results.size(), 4207, "Unexpected number of MCAS results generated");
    }
}
