package com.scholarscore.etl.powerschool.api.model.cycles;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.Cycle;
/**
 * Created by cwallace on 12/23/15.
 */
public class PsCycle implements IToApiModel<Cycle> {
    public Long dcid;
    public String day_name;
    public String letter;
    public Long schoolid;
    public Long day_number;
    public Long year_id;
    public Long id;
    public String abbreviation;


    @Override
    public Cycle toApiModel() {

        Cycle c = new Cycle();
        c.setAbbreviation(abbreviation);
        c.setDayName(day_name);
        c.setDayNumber(day_number);
        c.setDcid(dcid);
        c.setId(id);
        c.setLetter(letter);
        c.setSchoolNumber(schoolid);
        c.setYearId(year_id);
        return c;
    }
}
