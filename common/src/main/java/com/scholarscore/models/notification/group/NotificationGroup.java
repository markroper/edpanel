package com.scholarscore.models.notification.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.user.Person;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private Long schoolId;
    private NotificationGroupType type;
    private transient List<T> groupMembers;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Enumerated(EnumType.STRING)
    public NotificationGroupType getType() {
        return type;
    }

    public void setType(NotificationGroupType type) {
        this.type = type;
    }

    @JsonIgnore
    public List<T> getGroupMembers() {
        return groupMembers;
    }

    @JsonIgnore
    public void setGroupMembers(List<T> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(schoolId, groupMembers);
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
        return Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.groupMembers, other.groupMembers);
    }
}
