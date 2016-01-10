package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Student;

import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class SingleStudent extends NotificationGroup<Student> {
    private Long studentId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
