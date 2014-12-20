package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

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
    Long courseId;
    Long termId;
    Long yearId;
    
    Date startDate;
    Date endDate;
    String room;
    Set<Student> enrolledStudents;
    //TODO: List<Teacher> teachers;
    //TODO: Set<SectionAssignment> assignments;
    //TODO: Schedule
    //TODO: GradingScale
    //TODO: Gradebook - student -> SectionGrade { overallGrade, List<StudentAssignment>, homeworkGradeAvergae, quizGradeAvg }
    
    public Section() {
        super();
    }
    
    public Section(Section sect) {
        super(sect);
        courseId = sect.courseId;
        termId = sect.termId;
        yearId = sect.yearId;
        startDate = sect.startDate;
        endDate = sect.endDate;
        room = sect.room;
        enrolledStudents = sect.enrolledStudents;
    }
    
    @Override
    public void mergePropertiesIfNull(Section mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == courseId) {
            courseId = mergeFrom.courseId;
        }
        if(null == termId) {
            termId = mergeFrom.termId;
        }
        if(null == yearId) {
            yearId = mergeFrom.yearId;
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
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Section other = (Section) obj;
        return Objects.equals(this.courseId, other.courseId) && 
                Objects.equals(this.termId, other.termId) &&
                Objects.equals(this.yearId, other.yearId) &&
                Objects.equals(this.startDate, other.startDate) && 
                Objects.equals(this.endDate, other.endDate) &&
                Objects.equals(this.room, other.room) &&
                Objects.equals(this.enrolledStudents, other.enrolledStudents);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(courseId, termId, yearId, startDate, endDate, room, enrolledStudents);
    }
    
}
