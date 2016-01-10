package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Administrator;

import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class SingleAdministrator extends NotificationGroup<Administrator> {
    private Long administratorId;

    public Long getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Long administratorId) {
        this.administratorId = administratorId;
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
