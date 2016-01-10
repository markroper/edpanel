package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Teacher;

import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class SingleTeacher extends NotificationGroup<Teacher> {
    private Long teacherId;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(teacherId);
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
        final SingleTeacher other = (SingleTeacher) obj;
        return Objects.equals(this.teacherId, other.teacherId);
    }
}
