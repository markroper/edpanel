package com.scholarscore.etl.kickboard;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.Behavior;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by markroper on 4/1/16.
 */
public class KickboardBehavior implements IToApiModel<Behavior> {
    //"student id"
    public Long studentId;
    // "external id"
    public Long externalId;
    // "last name"
    public String lastName;
    // "first name"
    public String firstName;
    // "group id"
    public Long groupId;
    // "group name"
    public String groupName;
    // "behavior id"
    public Long behaviorId;
    // "behavior"
    public String behavior;
    // "category"
    public String category;
    // "comments"
    public String comments;
    // "merit points"
    public Long meritPoints;
    // "dollar points"
    public Double dollarPoints;
    // "date"
    public LocalDate date;
    // "timestamp"
    public LocalDateTime timestamp;
    // "staff id"
    public Long staffId;
    // "staff last name"
    public String staffLastName;
    // "staff first name"
    public String staffFirstName;
    // "school id"
    public Long schoolId;
    // "school name"
    public String schoolName;
    // "daily activity group id"
    public Long dailyActivityGroupId;
    // "daily activity group name"
    public String dailyActivityGroupName;
    // "bank purchase"
    public Double bankPurchase;
    //"incident id"
    public Long incidentId;

    @Override
    public Behavior toApiModel() {
        return null;
    }
}
