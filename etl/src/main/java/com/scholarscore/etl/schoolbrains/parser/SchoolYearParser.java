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
    public SchoolYearParser(File file) {
        super(file);
    }

    @Override
    public SchoolYear parseRec(CSVRecord rec) {
        SchoolYear schoolYear = new SchoolYear();
        // don't set edpanel id...
//        y.setId(parseLongOrReturnNull(rec.get(SchoolYearID)));
        // TODO SchoolBrains
        School sch = new School();
        sch.setSourceSystemId(rec.get(SchoolID));
        schoolYear.setSchool(sch);
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
