package com.scholarscore.models.attendance;

import java.util.ArrayList;

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
