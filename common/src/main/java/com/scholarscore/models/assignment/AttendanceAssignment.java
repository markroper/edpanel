    package com.scholarscore.models.assignment;

    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.scholarscore.models.HibernateConsts;

    import javax.persistence.DiscriminatorValue;
    import javax.persistence.Entity;
    import javax.persistence.Inheritance;
    import javax.persistence.InheritanceType;
    import javax.persistence.Table;
    import java.io.Serializable;

    /**
 * Expresses attendance to a single class on a specific date as a subclass of Assignment.
 * 
 * @author markroper
 * @see Assignment
 *
 */
@Entity
@Table(name = HibernateConsts.ASSIGNMENT_ASSIGNED_DATE)
@DiscriminatorValue(value = "AttendanceAssignment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceAssignment extends Assignment implements Serializable {
    public static final String ATTENDANCE = "ATTENDANCE";

    public AttendanceAssignment() {
        super(AssignmentType.ATTENDANCE);
    }
    
    public AttendanceAssignment(AttendanceAssignment assignment) {
        super(assignment);
    }

    @Override
    public void mergePropertiesIfNull(Assignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == mergeFrom || !(mergeFrom instanceof AttendanceAssignment)) {
            return;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class AttendanceAssignmentBuilder extends AssignmentBuilder<AttendanceAssignmentBuilder, AttendanceAssignment>{

        public AttendanceAssignment build(){
            AttendanceAssignment assignment = super.build();
            assignment.setType(AssignmentType.ATTENDANCE);
            return assignment;
        }

        @Override
        protected AttendanceAssignmentBuilder me() {
            return this;
        }

        @Override
        public AttendanceAssignment getInstance() {
            return new AttendanceAssignment();
        }
    }
}
