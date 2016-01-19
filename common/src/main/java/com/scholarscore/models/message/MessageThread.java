package com.scholarscore.models.message;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.message.topic.MessageTopic;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Objects;
import java.util.Set;

/**
 * Expresses a message thread or 'conversation'.  A thread can have one or many participants
 * participants can be added or removed without performance penalty.  Participants are able
 * to read any message on a thread.  Subthreads are not supported at this time.  Threads may have a topic
 * which is translated into a FK reference to an entity within EdPanel (e.g. a student's assignment, a notification,
 * and so on).
 *
 * Created by markroper on 1/17/16.
 */
@Entity(name = HibernateConsts.MESSAGE_THREAD_TABLE)
public class MessageThread {
    private Long id;
    private Set<MessageThreadParticipant> participants;
    private MessageTopic topic;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.MESSAGE_THREAD_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany
    @JoinColumn(name=HibernateConsts.MESSAGE_THREAD_FK, referencedColumnName=HibernateConsts.MESSAGE_THREAD_ID,  nullable = true)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    public Set<MessageThreadParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<MessageThreadParticipant> participants) {
        this.participants = participants;
    }

    @OneToOne
    @JoinColumn(name=HibernateConsts.MESSAGE_TOPIC_FK, nullable = true)
    @Cascade(CascadeType.ALL)
    public MessageTopic getTopic() {
        return topic;
    }

    public void setTopic(MessageTopic topic) {
        this.topic = topic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, participants, topic);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MessageThread other = (MessageThread) obj;
        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.participants, other.participants) &&
                Objects.equals(this.topic, other.topic);
    }
}
