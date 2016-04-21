package com.scholarscore.etl.schoolbrains.parser;

import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.time.LocalDate;

/**
 * Created by markroper on 4/14/16.
 */
public class SchoolYearParser extends MultiEntityCsvParser<SchoolYear> {

    @Override
    public SchoolYear parseRec(CSVRecord rec) {
        SchoolYear schoolYear = new SchoolYear();

        String schoolId = rec.get(SchoolID);
        
        // ('we', the ETL, doesn't set edpanel id - this is the job of the server
//        y.setId(parseLongOrReturnNull(rec.get(SchoolYearID)));
        // TODO SchoolBrains
//        School sch = new School();
//        sch.setSourceSystemId();
//        schoolYear.setSchool(sch);
        //TODO:figure out how to handle terms with schoolbrains
        //y.setTerms();
        String start = rec.get(SchoolYearStart);
        String[] st = start.split("\\s+");
        if(null != st && st.length > 0) {
            start = st[0];
        }
        schoolYear.setStartDate(LocalDate.parse(start, dtf));
        String end = rec.get(SchoolYearEnd);
        String[] ed = end.split("\\s+");
        if(null != ed && st.length > 0) {
            end = ed[0];
        }
        schoolYear.setEndDate(LocalDate.parse(end, dtf));
        return schoolYear;
    }
}
