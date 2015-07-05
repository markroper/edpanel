package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class AssignmentScore {
    String studentId;
    String scoreEntered;
    Integer possiblePoints;
    String comment;
    Boolean collected;
    Boolean missing;
    Boolean exempt;
    Boolean turnedInLate;
    String scoringType;
}
