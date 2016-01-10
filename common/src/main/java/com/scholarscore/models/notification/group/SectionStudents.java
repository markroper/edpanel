package com.scholarscore.models.notification.group;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;

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
@DiscriminatorValue(value = "SECTION_STUDENTS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SectionStudents extends NotificationGroup<Student> {
    private Long sectionId;

    @Column(name = HibernateConsts.SECTION_FK)
    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public NotificationGroupType getType() {
        return NotificationGroupType.SECTION_STUDENTS;
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
