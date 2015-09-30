package com.scholarscore.models;

import java.util.ArrayList;

import com.scholarscore.models.attendance.SchoolDay;

@SuppressWarnings("serial")
public class SchoolDayList extends ArrayList<SchoolDay>{
    public SchoolDayList() {
        super();
    }
    
    public SchoolDayList(ArrayList<SchoolDay> days) {
        super(days);
    }
    
    public SchoolDayList(SchoolDayList assignments) {
        super(assignments);
    }
}
