package com.scholarscore.models.notification.group;

import com.scholarscore.models.user.Student;

import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class SectionStudents extends NotificationGroup<Student> {
    private Long sectionId;

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(sectionId);
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
        final SectionStudents other = (SectionStudents) obj;
        return Objects.equals(this.sectionId, other.sectionId);
    }
}
