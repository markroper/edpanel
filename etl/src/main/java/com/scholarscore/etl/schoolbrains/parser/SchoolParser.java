package com.scholarscore.etl.schoolbrains.parser;

import com.scholarscore.models.School;
import org.apache.commons.csv.CSVRecord;

import java.io.File;

/**
 * User: jordan
 * Date: 4/20/16
 * Time: 11:55 AM
 */
public class SchoolParser extends BaseParser<School> {
    
    //## EdPanelSchools
    // SchoolID, SchoolName, DistrictID, DistrictName, DistrictState, GraduationRequirementCredits, PrincipalID
    private static final int SchoolID = 0;
    private static final int SchoolName = 1;
    private static final int DistrictID = 2;
    private static final int DistrictName = 3;
    private static final int DistrictState = 4;
    private static final int GraduationRequirementCredits = 5;
    private static final int PrincipalID = 6;

    @Override
    public School parseRec(CSVRecord rec) {
        School school = new School();
        school.setSourceSystemId(rec.get(SchoolID));
        school.setName(rec.get(SchoolName));
        return school;
    }
}
