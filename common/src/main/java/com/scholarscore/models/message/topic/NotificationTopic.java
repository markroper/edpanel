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
@DiscriminatorValue(value = NotificationTopic.NOTIFICATION_TOPIC)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class NotificationTopic extends MessageTopic {
    public static final String NOTIFICATION_TOPIC = "NOTIFICATION_TOPIC";

    @Override
    @Column(name = HibernateConsts.NOTIFICATION_FK)
    public Long getFk() {
        return this.fk;
    }

    @Override
    @Transient
    public TopicType getType() {
        return TopicType.NOTIFICATION;
    }
}
