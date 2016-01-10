package com.scholarscore.models.notification.group;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Teacher;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SINGLE_TEACHER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SingleTeacher extends NotificationGroup<Teacher> {
    private Long teacherId;

    @Column(name = HibernateConsts.TEACHER_FK)
    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public NotificationGroupType getType() {
        return NotificationGroupType.SINGLE_TEACHER;
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
