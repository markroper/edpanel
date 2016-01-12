package com.scholarscore.models.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
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
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * When a notification is triggered, one or many TriggeredNotification instances are created and stored.
 * A triggered notification associates a single Notification with a user to notify on a specific date, the date it was
 * triggered. This is to say that notification, triggeredDate and userIdToNotify are a unique key in the
 * underlying table.
 *
 * A triggered notification is considered active until it is dismissed or acted upon by the notified user.
 * It is considered positive if the change indicates an improvement and negative if the change indicates a decline.
 *
 * A triggered notification also stores the value that was calculated that exceeded the trigger defined on the
 * notification itself.
 *
 * Created by markroper on 1/10/16.
 */
@Entity(name = HibernateConsts.TRIGGERED_NOTIFICATION_TABLE)
@Table(name = HibernateConsts.TRIGGERED_NOTIFICATION_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriggeredNotification {
    private Long id;
    private Notification notification;
    private LocalDate triggeredDate;
    private Long userIdToNotify;
    private Boolean isActive;
    private Boolean isPositive;
    private Double valueWhenTriggered;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.TRIGGERED_NOTIFICATION_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name=HibernateConsts.NOTIFICATION_FK)
    @Fetch(FetchMode.JOIN)
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Column(name = HibernateConsts.TRIGGERED_NOTIFICATION_DATE)
    public LocalDate getTriggeredDate() {
        return triggeredDate;
    }

    public void setTriggeredDate(LocalDate triggeredDate) {
        this.triggeredDate = triggeredDate;
    }

    @Column(name = HibernateConsts.USER_FK)
    public Long getUserIdToNotify() {
        return userIdToNotify;
    }

    public void setUserIdToNotify(Long userIdToNotify) {
        this.userIdToNotify = userIdToNotify;
    }

    @Column(name = HibernateConsts.TRIGGERED_NOTIFICATION_ACTIVE)
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Column(name = HibernateConsts.TRIGGERED_NOTIFICATION_POSITIVE)
    public Boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(Boolean isPositive) {
        this.isPositive = isPositive;
    }

    @Column(name = HibernateConsts.TRIGGERED_NOTIFICATION_VALUE_WHEN_TRIGGERED)
    public Double getValueWhenTriggered() {
        return valueWhenTriggered;
    }

    public void setValueWhenTriggered(Double valueWhenTriggered) {
        this.valueWhenTriggered = valueWhenTriggered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, notification, triggeredDate, userIdToNotify, isActive, isPositive, valueWhenTriggered);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TriggeredNotification other = (TriggeredNotification) obj;
        return Objects.equals(this.notification, other.notification)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.triggeredDate, other.triggeredDate)
                && Objects.equals(this.userIdToNotify, other.userIdToNotify)
                && Objects.equals(this.isActive, other.isActive)
                && Objects.equals(this.isPositive, other.isPositive)
                && Objects.equals(this.valueWhenTriggered, other.valueWhenTriggered);
    }
}