package com.scholarscore.models.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 *  Expresses a single message posted by a user to a message thread.
 *
 * Created by markroper on 1/17/16.
 */
@Entity(name = HibernateConsts.MESSAGE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private Long id;
    private MessageThread thread;
    private LocalDateTime sent;
    private String body;
    private Long sentBy;
    private Set<MessageReadState> readStateList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.MESSAGE_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.MESSAGE_THREAD_FK)
    @Fetch(FetchMode.JOIN)
    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    @Column(name = HibernateConsts.MESSAGE_SENT)
    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }

    @Column(name = HibernateConsts.MESSAGE_BODY, columnDefinition="blob")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = HibernateConsts.USER_FK)
    public Long getSentBy() {
        return sentBy;
    }

    public void setSentBy(Long sentBy) {
        this.sentBy = sentBy;
    }

    @OneToMany
    @JoinColumn(name=HibernateConsts.MESSAGE_FK, referencedColumnName=HibernateConsts.MESSAGE_ID)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    public Set<MessageReadState> getReadStateList() {
        return readStateList;
    }

    public void setReadStateList(Set<MessageReadState> readStateList) {
        this.readStateList = readStateList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, thread, sent, body, sentBy, readStateList);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.thread, other.thread)
                && Objects.equals(this.sent, other.sent)
                && Objects.equals(this.body, other.body)
                && Objects.equals(this.sentBy, other.sentBy)
                && Objects.equals(this.readStateList, other.readStateList);
    }
}
