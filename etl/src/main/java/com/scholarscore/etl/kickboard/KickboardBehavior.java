package com.scholarscore.etl.kickboard;

import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Get yer POJOs out! Here is the object to map a CSV row from KickBoards behavior API into a POJO.
 * In addition to the raw POJO there is a method toInternalModel(..) that generates and returns an
 * EdPanel Behavior instance from the KickBoard model.
 *
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

    @SuppressWarnings("unchecked")
    public Behavior toApiModel(
            Map<Long, Student> studentAssociator,
            Map<String, List<Person>> firstNameToStaff,
            Map<String, List<Person>> lastNameToStaff) {
        BehaviorCategory cat = resolveBehaviorCategory(category);
        if(null == cat || null == date) {
            return null;
        }
        Student s = null;
        if(null != externalId) {
            s = studentAssociator.get(externalId);
        }
        if(null == s) {
            return null;
        }
        Staff staff = null;
        if(null != staffFirstName && null != staffLastName) {
            List<Person> firstName = firstNameToStaff.get(staffFirstName);
            if(null != firstName) {
                for(Person p: firstName) {
                    if(p.getName().contains(staffLastName)) {
                        staff = (Staff) p;
                        break;
                    }
                }
            }
            List<Person> lastName = lastNameToStaff.get(staffLastName);
            if(null == staff && null != lastName) {
                for(Person p: lastName) {
                    if(p.getName().contains(staffFirstName)) {
                        staff = (Staff) p;
                        break;
                    }
                }
            }
        }
        Behavior b = new Behavior();
        b.setStudent(s);
        b.setAssigner(staff);
        b.setPointValue(String.valueOf(meritPoints));
        b.setBehaviorCategory(cat);
        b.setBehaviorDate(date);
        b.setRemoteBehaviorId(String.valueOf(incidentId));
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
        } else if(CAT.contains("LUNCH DETENTION")) {
            return BehaviorCategory.LUNCH_DETENTION;
        } else if(CAT.contains("DETENTION")) {
            return BehaviorCategory.DETENTION;
        } else if(CAT.contains("SUSPENSION")) {
            return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION;
        } else if(CAT.contains("D.O.") || CAT.contains("OFFICE") || CAT.contains("REFERRAL") || CAT.contains("DEAN")) {
            return BehaviorCategory.REFERRAL;
        } else if(CAT.contains("AUTOMATIC") || CAT.contains("GENERAL") || CAT.contains("STORE")) {
            return null;
        } else {
            return BehaviorCategory.OTHER;
        }
    }
}
