package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A Section is a temporal instance of a Course.  Where a course defines that which is to be taught, a Section has
 * a reference to a course, and in addition, a start and end date, a set of enrolled students, a room, a grade book,
 * and so on.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section extends ApiModel implements Serializable, IApiModel<Section> {
    protected Date startDate;
    protected Date endDate;
    protected String room;
    protected GradeFormula gradeFormula;
    protected transient Course course;
    protected transient List<Student> enrolledStudents;
    protected transient List<SectionAssignment> sectionAssignments;
    //TODO: List<Teacher> teachers;
    //TODO: Set<SectionAssignment> assignments;
    //TODO: Schedule
    //TODO: Gradebook - student -> SectionGrade { overallGrade, List<StudentAssignment>, homeworkGradeAvergae, quizGradeAvg }
    
    public Section() {
        super();
    }
    
    public Section(Section sect) {
        super(sect);
        course = sect.course;
        startDate = sect.startDate;
        endDate = sect.endDate;
        room = sect.room;
        enrolledStudents = sect.enrolledStudents;
        sectionAssignments = sect.sectionAssignments;
        gradeFormula = sect.gradeFormula;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Student findEnrolledStudentById(Long id) {
        Student student = null;
        if(null != id && null != enrolledStudents && !enrolledStudents.isEmpty()) {
            for(Student s: enrolledStudents) {
                if(s.getId().equals(id)) {
                    student = s;
                    break;
                }
            }
        }
        return student;
    }
    
    public List<SectionAssignment> getSectionAssignments() {
        return sectionAssignments;
    }
    
    public SectionAssignment findAssignmentById(Long id) {
        SectionAssignment assignment = null;
        if(null != id && null != sectionAssignments && !sectionAssignments.isEmpty()) {
            for(SectionAssignment s: sectionAssignments) {
                if(s.getId().equals(id)) {
                    assignment = s;
                    break;
                }
            }
        }
        return assignment;
    }

    public void setSectionAssignments(List<SectionAssignment> sectionAssignments) {
        this.sectionAssignments = sectionAssignments;
    }

    public GradeFormula getGradeFormula() {
        return gradeFormula;
    }

    public void setGradeFormula(GradeFormula gradeFormula) {
        this.gradeFormula = gradeFormula;
    }

    @Override
    public void mergePropertiesIfNull(Section mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == course) {
            course = mergeFrom.course;
        }
        if(null == startDate) {
            startDate = mergeFrom.startDate;
        }
        if(null == endDate) {
            endDate = mergeFrom.endDate;
        }
        if(null == room) {
            room = mergeFrom.room;
        }
        if(null == enrolledStudents) {
            enrolledStudents = mergeFrom.enrolledStudents;
        }
        if(null == sectionAssignments) {
            sectionAssignments = mergeFrom.sectionAssignments;
        }
        if(null == gradeFormula) {
            gradeFormula = mergeFrom.gradeFormula;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Section other = (Section) obj;
        return Objects.equals(this.course, other.course) && 
                Objects.equals(this.startDate, other.startDate) && 
                Objects.equals(this.endDate, other.endDate) &&
                Objects.equals(this.room, other.room) &&
                Objects.equals(this.enrolledStudents, other.enrolledStudents) &&
                Objects.equals(this.sectionAssignments, other.sectionAssignments) &&
                Objects.equals(this.gradeFormula, other.gradeFormula);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(course, startDate, endDate, 
                room, enrolledStudents, sectionAssignments, gradeFormula);
    }
    
}
