package com.scholarscore.models.message;

import com.scholarscore.models.HibernateConsts;
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
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 1/17/16.
 */
@Entity(name = HibernateConsts.MESSAGE_THREAD_TABLE)
public class MessageThread {
    private Long id;
    private List<MessageThreadParticipant> participants;

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
    @JoinColumn(name=HibernateConsts.MESSAGE_THREAD_FK, referencedColumnName=HibernateConsts.MESSAGE_THREAD_ID)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    public List<MessageThreadParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MessageThreadParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, participants);
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
        return Objects.equals(this.id, other.id) && Objects.equals(this.participants, other.participants);
    }
}
