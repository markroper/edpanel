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
@DiscriminatorValue(value = GpaTopic.GPA_TOPIC)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class GpaTopic extends MessageTopic {
    public static final String GPA_TOPIC = "GPA";

    @Override
    @Column(name = HibernateConsts.GPA_FK)
    public Long getFk() {
        return this.fk;
    }

    @Override
    @Transient
    public TopicType getType() {
        return TopicType.GPA;
    }
}
