package com.scholarscore.models.attendance;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class AttendanceList extends ArrayList<Attendance>{
    public AttendanceList() {
        super();
    }
    
    public AttendanceList(ArrayList<Attendance> attendace) {
        super(attendace);
    }
    
    public AttendanceList(AttendanceList attendance) {
        super(attendance);
    }
}
