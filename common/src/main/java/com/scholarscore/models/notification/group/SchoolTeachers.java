package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Staff;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SCHOOL_TEACHERS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SchoolTeachers extends NotificationGroup<Staff> {
    @Override
    @Transient
    public NotificationGroupType getType() {
        return NotificationGroupType.SCHOOL_TEACHERS;
    }
}
