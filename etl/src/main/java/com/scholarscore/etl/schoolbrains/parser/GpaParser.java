package com.scholarscore.etl.schoolbrains.parser;

import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.gpa.SimpleGpa;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.time.LocalDate;

/**
 * Created by markroper on 4/14/16.
 */
public class GpaParser extends BaseParser<Gpa> {
    
    private static final int SASID = 0;
    private static final int STUDENT_ID = 1;
    protected static final int Gpa = 26;
    @Override
    public Gpa parseRec(CSVRecord rec) {
        SimpleGpa gpa = new SimpleGpa();
        gpa.setCalculationDate(LocalDate.now());
        gpa.setScore(parseDoubleOrReturnNull(rec.get(Gpa)));
        gpa.setStudentId(parseLongOrReturnNull(rec.get(STUDENT_ID)));
        return gpa;
    }
}
