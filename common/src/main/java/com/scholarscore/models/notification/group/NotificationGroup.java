package com.scholarscore.models.notification.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Person;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the concept of a group of people in the supported groupings for EdPanel notifications.  A
 * NotificationGroup can be the object of a notification (e.g. notify me when the average grade in geometry changes
 * by more than 5%) or the owner of a notification (notify all teachers in the school if some threshold is reached).
 * Eligible groups include a single teacher, student, all students in a section, all students in a grade, all students
 * in a school, all students by race, gender, ethnicity, ELL, special ed status, GPA range, HW completion rate, and so
 * on. Each of these examples would be expressed by a subclass of NotificationGroup.
 *
 * Created by markroper on 1/9/16.
 */
@Entity(name = HibernateConsts.NOTIFICATION_GROUP)
@Table(name = HibernateConsts.NOTIFICATION_GROUP)
@DiscriminatorColumn(name= HibernateConsts.NOTIFICATION_GROUP_TYPE, discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleStudent.class, name="SINGLE_STUDENT"),
        @JsonSubTypes.Type(value = SingleTeacher.class, name = "SINGLE_TEACHER"),
        @JsonSubTypes.Type(value = SingleAdministrator.class, name = "SINGLE_ADMINISTRATOR"),
        @JsonSubTypes.Type(value = SchoolTeachers.class, name = "SCHOOL_TEACHERS"),
        @JsonSubTypes.Type(value = SchoolAdministrators.class, name = "SCHOOL_ADMINISTRATORS"),
        @JsonSubTypes.Type(value = FilteredStudents.class, name = "FILTERED_STUDENTS"),
        @JsonSubTypes.Type(value = SectionStudents.class, name = "SECTION_STUDENTS")
})
public abstract class NotificationGroup<T extends Person> {
    private Long id;
    private transient List<T> groupMembers;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.NOTIFICATION_GROUP_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public abstract NotificationGroupType getType();

    public void setType(NotificationGroupType type) {
    }

    @JsonIgnore
    @Transient
    public List<T> getGroupMembers() {
        return groupMembers;
    }

    @JsonIgnore
    @Transient
    public void setGroupMembers(List<T> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupMembers, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NotificationGroup other = (NotificationGroup) obj;
        return Objects.equals(this.groupMembers, other.groupMembers)
                && Objects.equals(this.id, other.id);
    }
}
