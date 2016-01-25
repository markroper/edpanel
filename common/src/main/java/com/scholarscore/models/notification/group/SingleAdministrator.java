package com.scholarscore.models.notification.group;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Staff;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SINGLE_ADMINISTRATOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SingleAdministrator extends NotificationGroup<Staff> {
    private Long administratorId;

    @Column(name = HibernateConsts.ADMINISTRATOR_FK)
    public Long getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Long administratorId) {
        this.administratorId = administratorId;
    }

    @Override
    @Transient
    public NotificationGroupType getType() {
        return NotificationGroupType.SINGLE_ADMINISTRATOR;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(administratorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final SingleAdministrator other = (SingleAdministrator) obj;
        return Objects.equals(this.administratorId, other.administratorId);
    }
}
