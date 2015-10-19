package com.scholarscore.models.assignment;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class AssignmentList extends ArrayList<Assignment>{
    public AssignmentList() {
        super();
    }
    
    public AssignmentList(ArrayList<Assignment> assignments) {
        super(assignments);
    }
    
    public AssignmentList(AssignmentList assignments) {
        super(assignments);
    }
}
