package com.scholarscore.models.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.notification.group.NotificationGroup;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.User;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This class expresses the various forms that notifications can take within EdPanel.  A notification defines
 * the circumstances when a user or set of users should be notified about a change in data within EdPanel.
 * Examples of notifications include behaviors like 'notify me when any student's GPA changes by more than 5% over a
 * one month period' or 'notify me if the average grade in the geometry class I teach drops below a B-'.
 *
 * Created by markroper on 1/9/16.
 */
@Entity(name = HibernateConsts.NOTIFICATION_TABLE)
@Table(name = HibernateConsts.NOTIFICATION_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {
    private Long id;
    private String name;
    @NotNull
    private Long schoolId;
    //Can be null if the notification is unrelated to a single section
    private Section section;
    //Can be null if the notification is not assignment related
    private Assignment assignment;
    @NotNull
    //The creating user of the notification
    private User owner;
    @NotNull
    //The group of people who will be notified if the notification is triggered
    private NotificationGroup subscribers;
    @NotNull
    //The group of people we're calculating something about to potentially trigger a notification
    private NotificationGroup subjects;
    @NotNull
    //The value that causes the notification to be triggered. For example a notification for a class average of 80%
    //would have a trigger value of 0.8. A notification for a student getting 10 demerits in a period of time would
    //have a trigger value of 10.
    private Double triggerValue;
    @NotNull
    //If true, we trigger when the calculated value is greater than or equal to the trigger value
    //Otherwise, we trigger when the calculated value is less than or equal to the trigger value
    private Boolean triggeWhenGreaterThan = false;
    //For notifications based on groups of data, which aggregate function to use (e.g. average GPA, or sum of demerits)
    //If this aggregate function is null, the notification is not aggregate based, but rather, value based. For example
    //Notify me if a student's grade falls below 73%.  No aggregate function is required for this notification.
    private AggregateFunction aggregateFunction;
    //Can be null.  If not null, the notification is not triggering on a static value, but rather on a
    //value that changes over time.  This object contains a Duration and a boolean indicating whether or not
    //the trigger value should be treated as a percent change value, or the actual magnitude of the change in the value
    //from the beginning of the trigger period to the end.
    private NotificationWindow window;
    @NotNull
    //The entity that is being measured or triggered upon, e.g. GPA, grades, and so on
    private NotificationMeasure measure;
    @NotNull
    private LocalDate createdDate;
    @NotNull
    private LocalDate expiryDate;

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.ASSIGNMENT_FK)
    @Fetch(FetchMode.JOIN)
    public Assignment getAssignment() {
        return assignment;
    }

    @Column(name = HibernateConsts.NOTIFICATION_TRIGGER_GREATER_THAN)
    public Boolean getTriggeWhenGreaterThan() {
        return triggeWhenGreaterThan;
    }

    public void setTriggeWhenGreaterThan(Boolean triggeWhenGreaterThan) {
        this.triggeWhenGreaterThan = triggeWhenGreaterThan;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.SECTION_FK)
    @Fetch(FetchMode.JOIN)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.NOTIFICATION_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.NOTIFICATION_NAME)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.USER_FK)
    @Fetch(FetchMode.JOIN)
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @OneToOne
    @JoinColumn(name=HibernateConsts.NOTIFICATION_SUBSCRIBERS_FK)
    @Fetch(FetchMode.JOIN)
    @Cascade({ CascadeType.ALL })
    public NotificationGroup getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(NotificationGroup subscribers) {
        this.subscribers = subscribers;
    }

    @OneToOne
    @JoinColumn(name=HibernateConsts.NOTIFICATION_SUBJECTS_FK)
    @Fetch(FetchMode.JOIN)
    @Cascade({ CascadeType.ALL })
    public NotificationGroup getSubjects() {
        return subjects;
    }

    public void setSubjects(NotificationGroup subjects) {
        this.subjects = subjects;
    }

    @Column(name = HibernateConsts.NOTIFICATION_TRIGGER)
    public Double getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(Double triggerValue) {
        this.triggerValue = triggerValue;
    }

    @Column(name = HibernateConsts.NOTIFICATION_AGG_FUNCTION)
    @Enumerated(EnumType.STRING)
    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(AggregateFunction aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

    @Column(name = HibernateConsts.NOTIFICATION_WINDOW, columnDefinition="blob")
    public NotificationWindow getWindow() {
        return window;
    }

    public void setWindow(NotificationWindow window) {
        this.window = window;
    }

    @Column(name = HibernateConsts.NOTIFICATION_MEASURE)
    @Enumerated(EnumType.STRING)
    public NotificationMeasure getMeasure() {
        return measure;
    }

    public void setMeasure(NotificationMeasure measure) {
        this.measure = measure;
    }

    @Column(name = HibernateConsts.NOTIFICATION_CREATED_DATE)
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = HibernateConsts.NOTIFICATION_EXPIRY_DATE)
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscribers, subjects, owner, triggerValue, aggregateFunction,
                window, measure, createdDate, expiryDate, schoolId, section, assignment, triggeWhenGreaterThan);
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
        return Objects.equals(this.subscribers, other.subscribers)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.owner, other.owner)
                && Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.assignment, other.assignment)
                && Objects.equals(this.section, other.section)
                && Objects.equals(this.subjects, other.subjects)
                && Objects.equals(this.triggerValue, other.triggerValue)
                && Objects.equals(this.aggregateFunction, other.aggregateFunction)
                && Objects.equals(this.measure, other.measure)
                && Objects.equals(this.createdDate, other.createdDate)
                && Objects.equals(this.expiryDate, other.expiryDate)
                && Objects.equals(this.triggeWhenGreaterThan, other.triggeWhenGreaterThan)
                && Objects.equals(this.window, other.window);
    }
}
