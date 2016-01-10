package com.scholarscore.models.notification;

import com.scholarscore.models.notification.group.NotificationGroup;
import com.scholarscore.models.query.AggregateFunction;

import java.util.Objects;

/**
 * This base class represents the shared behavior that all notifications within EdPanel extend.  Concrete
 * examples of notifications include behaviors like 'notify me when any student's GPA changes by more than 5% over a
 * one month period' or 'notify me if the average grade in the geometry class I teach drops below a B-'.
 * Created by markroper on 1/9/16.
 */
public abstract class Notification {
    //The group of people who will be notified if the notification is triggered
    private NotificationGroup owners;
    //The group of people we're calculating something about to potentially trigger a notification
    private NotificationGroup subjects;
    //The value that causes the notification to be triggered. For example a notification for a class average of 80%
    // would have a trigger value of 0.8. A notification for a student getting 10 demerits in a period of time would
    // have a trigger value of 10.
    private Double triggerValue;
    //For notifications based on groups of data, which aggregate function to use (e.g. average GPA, or sum of demerits)
    //If this aggregate function is null, the notification is not aggregate based, but rather, value based. For example
    //Notify me if a student's grade falls below 73%.  No aggregate function is required for this notification.
    private AggregateFunction aggregateFunction;
    //Can be null.  If not null, the notification is not triggering on a static value, but rather on a
    //value that changes over time.  This object contains a Duration and a boolean indicating whether or not
    // the trigger value should be treated as a percent change value, or the actual magnitude of the change in the value
    //from the beginning of the trigger period to the end.
    private NotificationWindow window;

    public NotificationGroup getOwners() {
        return owners;
    }

    public void setOwners(NotificationGroup owners) {
        this.owners = owners;
    }

    public NotificationGroup getSubjects() {
        return subjects;
    }

    public void setSubjects(NotificationGroup subjects) {
        this.subjects = subjects;
    }

    public Double getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(Double triggerValue) {
        this.triggerValue = triggerValue;
    }

    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(AggregateFunction aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

    public NotificationWindow getWindow() {
        return window;
    }

    public void setWindow(NotificationWindow window) {
        this.window = window;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owners, subjects, triggerValue, aggregateFunction, window);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        return Objects.equals(this.owners, other.owners)
                && Objects.equals(this.subjects, other.subjects)
                && Objects.equals(this.triggerValue, other.triggerValue)
                && Objects.equals(this.aggregateFunction, other.aggregateFunction)
                && Objects.equals(this.window, other.window);
    }
}
