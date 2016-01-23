package com.scholarscore.models.message.topic;

import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * Created by markroper on 1/18/16.
 */
@Entity
@DiscriminatorValue(value = SectionGradeTopic.SECTION_GRADE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SectionGradeTopic extends MessageTopic {
    public static final String SECTION_GRADE = "SECTION_GRADE";
    @Override
    @Column(name = HibernateConsts.STUDENT_SECTION_GRADE_FK)
    public Long getFk() {
        return this.fk;
    }

    @Override
    @Transient
    public TopicType getType() {
        return TopicType.SECTION_GRADE;
    }
    
    
}
