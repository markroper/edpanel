package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Administrator;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SCHOOL_ADMINISTRATORS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SchoolAdministrators extends NotificationGroup<Administrator> {
    @Override
    public NotificationGroupType getType() {
        return NotificationGroupType.SCHOOL_ADMINISTRATORS;
    }
}
