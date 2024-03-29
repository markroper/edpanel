package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.SchoolYear;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = {"functional"})
public class SchoolYearJdbcTest extends BaseJdbcTest {

    public void testSchoolYearCrud() {
        SchoolYear createSchoolYear = createSchoolYear();
        Long schoolId = createSchoolYear.getSchool().getId();
        Long schoolYearId = createSchoolYear.getId();
        assertNotNull(schoolYearId, "Unexpected null schoolYearId from insert method call");

        SchoolYear selectSchoolYear = schoolYearDao.select(schoolId, schoolYearId);
// TODO: end + start won't match since the value from the DB is timestamp and the value created via the test is java.util.Date...
//        assertEquals(selectSchoolYear.getEndDate(), createSchoolYear.getEndDate().getTime(), "Unexpected End Date Equality mis-match");
//        assertEquals(selectSchoolYear.getStartDate().getTime(), createSchoolYear.getStartDate(), "Unexpected End Date Equality mis-match");
        assertEquals(selectSchoolYear.getId(), createSchoolYear.getId(), "Unexpected id mismatch for school year");
        assertEquals(selectSchoolYear.getName(), createSchoolYear.getName(), "Unexpected name difference");
        assertEquals(selectSchoolYear.getSchool(), createSchoolYear.getSchool(), "Unexpected school difference");
        deleteSchoolYearAndVerify();
    }
}
