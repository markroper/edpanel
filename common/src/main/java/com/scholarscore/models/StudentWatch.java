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
 * This entity maintains a many to many relationship between students and teachers
 * for the purpose of allowing teachers to watch certain students. While this could be
 * accomplished with hibernate, it woudl require a collection of students to be stored on each staff object
 * and a collection of staff to be stored on every student.
 * The fear was that this would negatively impact the query time of the frequently run "get a student or staff" query
 * Instead, this separate entity allows us to maintain this relationship, but without affecting the structure of
 * student or staff objects. The cost to this is that for instance to get the goals of all students that a teacher is
 * watching, two queries are required instead of one. One to get all the watches the staff has and one to get all
 * the goals that group of students has. Since this is done infrequently the cost of the additional query was determined
 * to be more worth it.
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
    };
}
