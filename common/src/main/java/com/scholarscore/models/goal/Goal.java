package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract class defining common fields and methods that exist across all goals.
 * Created by cwallace on 9/20/2015.
 */
@SuppressWarnings("serial")
@Entity(name = HibernateConsts.GOAL_TABLE)
@DiscriminatorColumn(name=HibernateConsts.GOAL_TYPE, discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "goalType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehaviorGoal.class, name="BEHAVIOR"),
        @JsonSubTypes.Type(value = AssignmentGoal.class, name = "ASSIGNMENT"),
        @JsonSubTypes.Type(value = SectionGradeGoal.class, name = "SECTION_GRADE"),
        @JsonSubTypes.Type(value = AttendanceGoal.class, name = "ATTENDANCE")
})
public abstract class Goal extends ApiModel implements IApiModel<Goal>, IGoal {

    private Student student;
    private Staff staff;
    private Double desiredValue;
    private Double calculatedValue;
    private Boolean approved;
    private GoalType goalType;
    private LocalDate startDate;
    private LocalDate endDate;

    private GoalProgress goalProgress;
    private Boolean autocomplete;
    private String plan;
    private Boolean teacherFollowup;

    public Goal() {
        super();
        //THis is maybe controversial. Goals should not be completed upon creation and a teacher followup
        //Should not have already happened. So we will set them to initial conditions
        this.teacherFollowup = Boolean.FALSE;
        this.goalProgress = GoalProgress.IN_PROGRESS;

    }

    public Goal(Goal goal) {
        super(goal);
        this.student = goal.student;
        this.staff = goal.staff;
        this.desiredValue = goal.desiredValue;
        this.calculatedValue = goal.calculatedValue;
        this.approved = goal.approved;
        this.setGoalType(goal.goalType);
        this.goalProgress = goal.goalProgress;
        this.autocomplete = goal.autocomplete;
        this.startDate = goal.startDate;
        this.endDate = goal.endDate;
        this.plan = goal.plan;
        this.teacherFollowup = goal.teacherFollowup;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.GOAL_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = HibernateConsts.GOAL_PROGRESS, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    public GoalProgress getGoalProgress() {
        return goalProgress;
    }

    public void setGoalProgress(GoalProgress goalProgress) {
        this.goalProgress = goalProgress;
    }

    @Column(name = HibernateConsts.GOAL_AUTOCOMPLETE)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public Boolean getAutocomplete() {
        return autocomplete;
    }

    public void setAutocomplete(Boolean autocomplete) {
        this.autocomplete = autocomplete;
    }

    @Column(name = HibernateConsts.GOAL_START_DATE)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.GOAL_END_DATE)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Column(name = HibernateConsts.GOAL_PLAN, columnDefinition = "blob")
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    @Column(name = HibernateConsts.GOAL_FOLLOWUP)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public Boolean getTeacherFollowup() {
        return teacherFollowup;
    }

    public void setTeacherFollowup(Boolean teacherFollowup) {
        this.teacherFollowup = teacherFollowup;
    }

    @Override
    @Column(name = HibernateConsts.GOAL_NAME)
    public String getName() {
        return super.getName();
    }




    @Column(name = HibernateConsts.GOAL_APPROVED)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }



    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.STUDENT_FK, nullable = true)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.STAFF_FK, nullable = true)
    public Staff getStaff() {
        return staff;
    }

    public void setCalculatedValue(Double value) {
        this.calculatedValue = value;
    }

    @Transient
    public Double getCalculatedValue() {
        return calculatedValue;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Column(name = HibernateConsts.DESIRED_GOAL_VALUE)
    public Double getDesiredValue() {
        return desiredValue;
    }

    public void setDesiredValue(Double desiredValue) {
        this.desiredValue = desiredValue;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    @Column(name = HibernateConsts.GOAL_TYPE, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    public GoalType getGoalType() {
        return goalType;
    }




    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == desiredValue) {
            this.desiredValue = mergeFrom.desiredValue;
        }
        if (null == calculatedValue) {
            this.calculatedValue = mergeFrom.calculatedValue;
        }
        if (null == approved) {
            this.approved = mergeFrom.approved;
        }
        if (null == goalType) {
            setGoalType(mergeFrom.goalType);
        }
        if (null == student) {
            this.student = mergeFrom.student;
        }
        if (null == staff) {
            this.staff = mergeFrom.staff;
        }
        if (null == goalProgress) {
            this.goalProgress = mergeFrom.goalProgress;
        }
        if (null == autocomplete) {
            this.autocomplete = mergeFrom.autocomplete;
        }
        if (null == plan) {
            this.plan = mergeFrom.plan;
        }
        if (null == teacherFollowup) {
            this.teacherFollowup = mergeFrom.teacherFollowup;
        }

        if (null == startDate) {
            this.startDate = mergeFrom.startDate;
        }

        if (null == endDate) {
            this.endDate = mergeFrom.endDate;
        }
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(student, staff, desiredValue, calculatedValue, approved, goalType, startDate, endDate, goalProgress, autocomplete, plan, teacherFollowup);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Goal other = (Goal) obj;
        return Objects.equals(this.student, other.student)
                && Objects.equals(this.staff, other.staff)
                && Objects.equals(this.desiredValue, other.desiredValue)
                && Objects.equals(this.calculatedValue, other.calculatedValue)
                && Objects.equals(this.approved, other.approved)
                && Objects.equals(this.goalType, other.goalType)
                && Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.goalProgress, other.goalProgress)
                && Objects.equals(this.autocomplete, other.autocomplete)
                && Objects.equals(this.plan, other.plan)
                && Objects.equals(this.teacherFollowup, other.teacherFollowup);
    }

    @Override
    public String toString() {
        return
                "GOAL super(" + super.toString() +")" + "\n"
                        + "Id  : " + getId() + "\n"
                        + "Name: " + getName() + "\n"
                        + "DesiredValue: " + getDesiredValue() +"\n"
                        + "CalculatedValue: " + getCalculatedValue() + "\n"
                        + "Approved: " + getApproved() + "\n"
                        + "GoalType: " + getGoalType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Staff: " + getStaff() + "\n"
                        + "StartDate: " + getStartDate() + "\n"
                        + "EndDate: " + getEndDate() + "\n"
                        + "GoalProgress: " + getGoalProgress() + "\n"
                        + "Autocomplete: " + getAutocomplete() + "\n"
                        + "Plan: " + getPlan() + "\n"
                        + "TeacherFollowup: " + getTeacherFollowup() + "\n";
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class GoalBuilder<U extends GoalBuilder<U, T>, T extends Goal> extends ApiModelBuilder<U,T>{

        private Student student;
        private Staff staff;
        private Double desiredValue;
        private Double calculatedValue;
        private Boolean approved;
        private GoalType goalType;
        private Boolean autocomplete;
        private String plan;

        public U withAutoComplete(final Boolean autoComplete) {
            this.autocomplete = autoComplete;
            return me();
        }

        public U withPlan(final String plan) {
            this.plan = plan;
            return me();
        }

        public U withStudent(final Student student){
            this.student = student;
            return me();
        }

        public U withStaff(final Staff person){
            this.staff = person;
            return me();
        }

        public U withDesiredValue(final Double desiredValue){
            this.desiredValue = desiredValue;
            return me();
        }

        public U withCalculatedValue(final Double calculatedValue){
            this.calculatedValue = calculatedValue;
            return me();
        }

        public U withApproved(final Boolean approved){
            this.approved = approved;
            return me();
        }

        public U withGoalType(final GoalType goalType){
            this.goalType = goalType;
            return me();
        }

        public T build(){
            T goal = super.build();
            goal.setStudent(student);
            goal.setStaff(staff);
            goal.setPlan(plan);
            goal.setAutocomplete(autocomplete);
            goal.setDesiredValue(desiredValue);
            goal.setCalculatedValue(calculatedValue);
            goal.setApproved(approved);
            goal.setGoalType(goalType);
            goal.setAutocomplete(autocomplete);
            return goal;
        }
    }

}
