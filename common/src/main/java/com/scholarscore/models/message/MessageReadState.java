package com.scholarscore.models.message;

import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Inserted only when a user has read a message.  This would mean
 * not that a GET is called on the message for a given user, but that
 * a 'confirm read' API call has actually be called for that message.
 *
 * Created by markroper on 1/17/16.
 */
@Entity(name = HibernateConsts.MESSAGE_READ_STATE_TABLE)
@IdClass(MessageReadState.ReadStatePk.class)
public class MessageReadState {
    private Long messageId;
    private Long participantId;
    private LocalDateTime readOn;

    @Id
    @Column(name = HibernateConsts.MESSAGE_FK)
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Id
    @Column(name = HibernateConsts.USER_FK)
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Column(name = HibernateConsts.MESSAGE_READ_STATE_ON)
    public LocalDateTime getReadOn() {
        return readOn;
    }

    public void setReadOn(LocalDateTime readOn) {
        this.readOn = readOn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, participantId, readOn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MessageReadState other = (MessageReadState) obj;
        return Objects.equals(this.messageId, other.messageId)
                && Objects.equals(this.participantId, other.participantId)
                && Objects.equals(this.readOn, other.readOn);
    }
    public static class ReadStatePk implements Serializable {
        protected Long messageId;
        protected Long participantId;

        public ReadStatePk() {

        }

        @Column(name = HibernateConsts.MESSAGE_FK)
        public Long getMessageId() {
            return messageId;
        }

        public void setMessageId(Long messageId) {
            this.messageId = messageId;
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
            return Objects.hash(messageId, participantId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ReadStatePk other = (ReadStatePk) obj;    
            return Objects.equals(this.messageId, other.messageId)
                    && Objects.equals(this.participantId, other.participantId);
        }
    }
}
