package com.scholarscore.etl.kickboard;

import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by markroper on 4/1/16.
 */
public class KickboardBehavior {
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

    public Behavior toApiModel(StudentAssociator studentAssociator, StaffAssociator staffAssociator) {
        BehaviorCategory cat = resolveBehaviorCategory(category);
        if(null == cat) {
            return null;
        }
        Behavior b = new Behavior();
        b.setPointValue(String.valueOf(meritPoints));
        b.setBehaviorCategory(cat);
        b.setBehaviorDate(date);
        b.setRemoteBehaviorId(String.valueOf(behaviorId));
        b.setRemoteSystem("Kickboard");
        b.setName(behavior);
        return b;
    }

    private static BehaviorCategory resolveBehaviorCategory(String category) {
        if(null == category) {
            return null;
        }
        String CAT = category.toUpperCase();
        if(CAT.contains("DEMERIT")) {
            return BehaviorCategory.DEMERIT;
        } else if(CAT.contains("MERIT")) {
            return BehaviorCategory.MERIT;
        } else if(CAT.contains("HOMEWORK")) {
            return BehaviorCategory.HOMEWORK;
        } else if(CAT.contains("DETENTION")) {
            return BehaviorCategory.DETENTION;
        } else if(CAT.contains("SUSPENSION")) {
            return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION;
        } else if(CAT.contains("D.O.") || CAT.contains("OFFICE") || CAT.contains("REFERRAL")) {
            return BehaviorCategory.REFERRAL;
        } else if(CAT.contains("AUTOMATIC")) {
            return null;
        } else {
            return BehaviorCategory.OTHER;
        }
    }
}
