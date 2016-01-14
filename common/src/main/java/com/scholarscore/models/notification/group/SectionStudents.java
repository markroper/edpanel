package com.scholarscore.models.notification.group;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Student;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "SECTION_STUDENTS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SectionStudents extends NotificationGroup<Student> {
    private Section section;

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.SECTION_FK)
    @Fetch(FetchMode.JOIN)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    @Transient
    public NotificationGroupType getType() {
        return NotificationGroupType.SECTION_STUDENTS;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(section);
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
        return Objects.equals(this.section, other.section);
    }
}
