package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Teacher;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SCHOOL_TEACHERS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SchoolTeachers extends NotificationGroup<Teacher> {
    @Override
    public NotificationGroupType getType() {
        return NotificationGroupType.SCHOOL_TEACHERS;
    }
}
