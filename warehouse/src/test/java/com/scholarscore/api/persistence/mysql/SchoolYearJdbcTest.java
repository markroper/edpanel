package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = {"functional"})
public class SchoolYearJdbcTest extends BaseJdbcTest {

    public void testSchoolYearCrud() {
        Long schoolId = schoolDao.createSchool(school);
        School createdSchool = schoolDao.selectSchool(schoolId);

        SchoolYear createSchoolYear = new SchoolYear(schoolYear);
        createSchoolYear.setSchool(createdSchool);
        Long schoolYearId = schoolYearDao.insert(schoolId, createSchoolYear);
        assertNotNull(schoolYearId, "Unexpected null schoolYearId from insert method call");

        SchoolYear selectSchoolYear = schoolYearDao.select(schoolId, schoolYearId);
// TODO: end + start won't match since the value from the DB is timestamp and the value created via the test is java.util.Date...
//        assertEquals(selectSchoolYear.getEndDate(), createSchoolYear.getEndDate().getTime(), "Unexpected End Date Equality mis-match");
//        assertEquals(selectSchoolYear.getStartDate().getTime(), createSchoolYear.getStartDate(), "Unexpected End Date Equality mis-match");
        assertEquals(selectSchoolYear.getId(), createSchoolYear.getId(), "Unexpected id mismatch for school year");
        assertEquals(selectSchoolYear.getName(), createSchoolYear.getName(), "Unexpected name difference");
        assertEquals(selectSchoolYear.getSchool(), createSchoolYear.getSchool(), "Unexpected school difference");

        schoolYearDao.delete(schoolYearId);

        assertNull(schoolYearDao.select(schoolId, schoolYearId), "Expected school year to be null after delete method call");
    }


}
