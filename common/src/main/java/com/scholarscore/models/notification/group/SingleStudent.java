package com.scholarscore.models.notification.group;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;

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
@DiscriminatorValue(value = "SINGLE_STUDENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SingleStudent extends NotificationGroup<Student> {
    private Long studentId;

    @Column(name = HibernateConsts.STUDENT_FK)
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Override
    @Transient
    public NotificationGroupType getType() {
        return NotificationGroupType.SINGLE_STUDENT;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(studentId);
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
        final SingleStudent other = (SingleStudent) obj;
        return Objects.equals(this.studentId, other.studentId);
    }
}
