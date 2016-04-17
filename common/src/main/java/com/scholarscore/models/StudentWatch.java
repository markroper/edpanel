package com.scholarscore.models;

import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;

/**
 * Created by cwallace on 4/14/16.
 */
@Entity(name = HibernateConsts.WATCH_TABLE)
public class StudentWatch {
    private Staff staff;
    private Student student;
    private long id;

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.STAFF_FK, nullable = true)
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.STUDENT_FK, nullable = true)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.WATCH_ID)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(staff, student, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StudentWatch other = (StudentWatch) obj;
        return Objects.equals(this.staff, other.staff)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "StudentWatch{" +
                "staff=" + staff +
                ", student=" + student +
                ", id=" + id +
                '}';
    }

    //Query is basically like, join watch table student_fk = student_fk select all from goal where w.tacher_fk =
}
