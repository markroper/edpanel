package com.scholarscore.models.message;

import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user who is party to a given message thread.
 *
 * Created by markroper on 1/17/16.
 */
@Entity(name = HibernateConsts.MESSAGE_THREAD_PARTICIPANT_TABLE)
@IdClass(MessageThreadParticipant.ParticipantPk.class)
public class MessageThreadParticipant {
    private Long threadId;
    private Long participantId;

    @Id
    @Column(name = HibernateConsts.MESSAGE_THREAD_FK)
    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    @Id
    @Column(name = HibernateConsts.USER_FK)
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadId, participantId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MessageThreadParticipant other = (MessageThreadParticipant) obj;
        return Objects.equals(this.threadId, other.threadId)
                && Objects.equals(this.participantId, other.participantId);
    }

    public static class ParticipantPk implements Serializable {
        protected Long threadId;
        protected Long participantId;

        public ParticipantPk() {
        }

        @Column(name = HibernateConsts.MESSAGE_THREAD_FK)
        public Long getThreadId() {
            return threadId;
        }

        public void setThreadId(Long threadId) {
            this.threadId = threadId;
        }

        @Column(name = HibernateConsts.USER_FK)
        public Long getParticipantId() {
            return participantId;
        }

        public void setParticipantId(Long participantId) {
            this.participantId = participantId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(threadId, participantId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ParticipantPk other = (ParticipantPk) obj;
            return Objects.equals(this.threadId, other.threadId)
                    && Objects.equals(this.participantId, other.participantId);
        }
    }
}
