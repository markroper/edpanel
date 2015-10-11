package com.scholarscore.models.attendance;

import java.io.Serializable;
import java.util.Objects;

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
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;

/**
 * Represents school attendance for a single student on a single day.
 * 
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.ATTENDANCE_TABLE)
@Table(name = HibernateConsts.ATTENDANCE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public class Attendance implements Serializable {
    private SchoolDay schoolDay;
    private Long id;
    private Student student;
    private AttendanceStatus status;
    private String description;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name=HibernateConsts.SCHOOL_DAY_FK)
    @Fetch(FetchMode.JOIN)
    public SchoolDay getSchoolDay() {
        return schoolDay;
    }
    public void setSchoolDay(SchoolDay schoolDay) {
        this.schoolDay = schoolDay;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.ATTENDANCE_ID)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name=HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = HibernateConsts.ATTENDANCE_STATUS)
    public AttendanceStatus getStatus() {
        return status;
    }
    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    @Column(name = HibernateConsts.ATTENDANCE_DESCRIPTION)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Attendance other = (Attendance) obj;
        return Objects.equals(this.schoolDay, other.schoolDay)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.status, other.status)
                && Objects.equals(this.description, other.description);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(schoolDay, id, student, status, description);
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public class AttendanceBuilder {

        private SchoolDay schoolDay;
        private Long id;
        private Student student;
        private AttendanceStatus status;
        private String description;

        public AttendanceBuilder withSchoolDay(final SchoolDay schoolDay){
            this.schoolDay = schoolDay;
            return this;
        }

        public AttendanceBuilder withId(final Long id){
            this.id = id;
            return this;
        }

        public AttendanceBuilder withStudent(final Student student){
            this.student = student;
            return this;
        }

        public AttendanceBuilder withAttendanceStatus(final AttendanceStatus status){
            this.status = status;
            return this;
        }

        public AttendanceBuilder withDescription(final String description){
            this.description = description;
            return this;
        }

        public Attendance build(){
            Attendance attendance = new Attendance();
            attendance.setSchoolDay(schoolDay);
            attendance.setId(id);
            attendance.setStudent(student);
            attendance.setStatus(status);
            attendance.setDescription(description);
            return attendance;
        }
    }
}
