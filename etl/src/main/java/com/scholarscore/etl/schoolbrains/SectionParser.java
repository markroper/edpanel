package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Section;
import org.apache.commons.csv.CSVRecord;

import java.io.File;

/**
 * Created by markroper on 4/14/16.
 */
public class SectionParser extends MultiEntityCsvParser<Section> {
    public SectionParser(File file) {
        super(file);
    }

    @Override
    public Section parseRec(CSVRecord rec) {
        return null;
    }
}
