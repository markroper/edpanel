package com.scholarscore.models.message.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * Created by markroper on 1/18/16.
 */
@Entity(name = HibernateConsts.MESSAGE_TOPIC_TABLE)
@DiscriminatorColumn(name= HibernateConsts.MESSAGE_TOPIC_TYPE, discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AssignmentTopic.class, name= AssignmentTopic.ASSIGNMENT_TOPIC),
        @JsonSubTypes.Type(value = BehaviorTopic.class, name = BehaviorTopic.BEHAVIOR_TOPIC),
        @JsonSubTypes.Type(value = GpaTopic.class, name = GpaTopic.GPA_TOPIC),
        @JsonSubTypes.Type(value = NotificationTopic.class, name = NotificationTopic.NOTIFICATION_TOPIC),
        @JsonSubTypes.Type(value = SectionGradeTopic.class, name = SectionGradeTopic.SECTION_GRADE)
})
public abstract class MessageTopic {
    private Long id;
    protected Long fk;
    private Long schoolId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.MESSAGE_TOPIC_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * Subclasses should implement and annotate with the correct hibernate FK
     * @return
     */
    @Transient
    public abstract Long getFk();

    public void setFk(Long fk) {}

    @Transient
    public abstract TopicType getType();

    public void setType(TopicType type) {
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MessageTopic other = (MessageTopic) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.fk, other.fk) && Objects.equals(this.schoolId, other.schoolId);
    }
}
