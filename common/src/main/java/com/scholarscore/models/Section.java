package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelObjectMapper;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Section is a temporal instance of a Course.  Where a course defines that which is to be taught, a Section has
 * a reference to a course, and in addition, a start and end date, a set of enrolled students, a room, a grade book,
 * and so on.
 *
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.SECTION_TABLE)
@Table(name = HibernateConsts.SECTION_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section extends ApiModel implements Serializable, IApiModel<Section> {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String room;

    /**
     * This is a schedule expression. It represents what period(s) this Section is taught on on a particular
     * cycle letter. The key is the Cycle Letter and the value is an Array List of Longs for each period_number
     * that the section is taught on that particular day.
     */
    protected Map<String,ArrayList<Long>> expression;
    //For jackson & for java (hibernate uses different getter to access the string value)
    protected GradeFormula gradeFormula;
    //The starting term
    protected Term term;
    protected Integer numberOfTerms;
    protected transient Course course;
    protected transient List<Student> enrolledStudents;
    protected transient List<Assignment> assignments;
    protected Set<Staff> teachers;
    protected String sourceSystemId;
    
    public Section() {
        super();
        enrolledStudents = Lists.newArrayList();
        assignments = Lists.newArrayList();
        teachers = Sets.newHashSet();
    }

    public Section(LocalDate startDate, LocalDate endDate, String room, GradeFormula gradeFormula, Integer numberOfTerms, Map<String,ArrayList<Long>> expression) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.room = room;
        setGradeFormula(gradeFormula);
        this.numberOfTerms = numberOfTerms;
        this.expression = expression;
    }

    public Section(Section sect) {
        super(sect);
        course = sect.course;
        startDate = sect.startDate;
        endDate = sect.endDate;
        room = sect.room;
        enrolledStudents = sect.enrolledStudents;
        assignments = sect.assignments;
        gradeFormula = sect.gradeFormula;
        sourceSystemId = sect.sourceSystemId;
        numberOfTerms = sect.numberOfTerms;
        this.expression = sect.expression;
        this.teachers = sect.teachers;
        this.term = sect.term;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = HibernateConsts.STAFF_TABLE,
            joinColumns = { @JoinColumn(name = HibernateConsts.SECTION_FK, nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = HibernateConsts.STAFF_FK, nullable = false, updatable = false) })
    @Fetch(FetchMode.JOIN)
    public Set<Staff> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Staff> staffs) {
        this.teachers = staffs;
    }

    public void addPerson(Staff person) {
        this.teachers.add(person);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.SECTION_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.SECTION_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.COURSE_FK)
    @Fetch(FetchMode.JOIN)
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.TERM_FK)
    @Fetch(FetchMode.JOIN)
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Column(name = HibernateConsts.SECTION_START_DATE)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.SECTION_END_DATE)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Column(name = HibernateConsts.SECTION_ROOM)
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @JsonIgnore
    @Column(name = HibernateConsts.SECTION_GRADE_FORMULA)
    public String getGradeFormulaString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(gradeFormula);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @JsonIgnore
    public void setGradeFormulaString(String string) {
        if(null == string) {
            this.gradeFormula = null;
        } else {
            try {
                this.gradeFormula = EdPanelObjectMapper.MAPPER.readValue( string, new TypeReference<GradeFormula>(){});
            } catch (IOException e) {
                this.gradeFormula =  null;
            }
        }
    }

    @Transient
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void addEnrolledStudent(Student enrolledStudent){
        enrolledStudents.add(enrolledStudent);
    }

    @Column(name = HibernateConsts.SECTION_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
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

    @Transient
    public List<Assignment> getAssignments() {
        return assignments;
    }

    public Assignment findAssignmentById(Long id) {
        Assignment assignment = null;
        if(null != id && null != assignments && !assignments.isEmpty()) {
            for(Assignment s: assignments) {
                if(s.getId().equals(id)) {
                    assignment = s;
                    break;
                }
            }
        }
        return assignment;
    }

    @Column(name = HibernateConsts.SECTION_NUMBER_OF_TERMS)
    public Integer getNumberOfTerms() {
        return numberOfTerms;
    }

    public void setNumberOfTerms(Integer numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
    }

    @Transient
    public GradeFormula getGradeFormula() {
        return gradeFormula;
    }

    public void setGradeFormula(GradeFormula gradeFormula) {
        this.gradeFormula = gradeFormula;
    }


    @JsonIgnore
    @Column(name = HibernateConsts.SECTION_EXPRESION, columnDefinition="blob")
    public String getExpressionString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(expression);
        } catch (JsonProcessingException | NullPointerException e) {
            return null;
        }
    }
    @JsonIgnore
    public void setExpressionString(String gradesString) {
        try {
            this.expression = EdPanelObjectMapper.MAPPER.readValue(
                    gradesString, new TypeReference<HashMap<String, ArrayList<Long>>>(){});
        } catch (IOException | NullPointerException e) {
            this.expression = null;
        }
    }

    @Transient
    public Map<String, ArrayList<Long>> getExpression() {
        return expression;
    }

    public void setExpression(Map<String, ArrayList<Long>> expression) {
        this.expression = expression;

    }

    @Override
    public void mergePropertiesIfNull(Section mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == mergeFrom) { return; }
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
        if(null == assignments) {
            assignments = mergeFrom.assignments;
        }
        if(null == gradeFormula) {
            this.gradeFormula = mergeFrom.gradeFormula;
        }
        if(null == sourceSystemId) {
            sourceSystemId = mergeFrom.sourceSystemId;
        }
        if(null == numberOfTerms) {
            numberOfTerms = mergeFrom.numberOfTerms;
        }
        if(null == term) {
            term = mergeFrom.term;
        }
        if (null == expression) {
            expression = mergeFrom.expression;
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
                Objects.equals(this.assignments, other.assignments) &&
                Objects.equals(this.sourceSystemId, other.sourceSystemId) &&
                Objects.equals(this.numberOfTerms, other.numberOfTerms) &&
                Objects.equals(this.gradeFormula, other.gradeFormula) && 
                Objects.equals(this.term, other.term) &&
                Objects.equals(this.teachers, other.teachers) &&
                Objects.equals(this.expression, other.expression);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(course, startDate, endDate, sourceSystemId,
                room, enrolledStudents, assignments, gradeFormula, numberOfTerms, term, teachers, expression);
    }

    @Override
    public String toString() {
        return "Section{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", room='" + room + '\'' +
                ", gradeFormula=" + gradeFormula +
                ", term=" + term +
                ", course=" + course +
                ", enrolledStudents=" + enrolledStudents +
                ", assignments=" + assignments +
                ", teachers=" + teachers +
                ", numberOfTerms=" + numberOfTerms +
                ", sourceSystemId='" + sourceSystemId  +
                ", expression='" + expression + '\'' +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class SectionBuilder extends ApiModelBuilder<SectionBuilder, Section> {

        protected LocalDate startDate;
        protected LocalDate endDate;
        protected String room;
        protected GradeFormula gradeFormula;
        protected Term term;
        protected transient Course course;
        protected transient List<Student> enrolledStudents;
        protected transient List<Assignment> assignments;
        protected List<StudentSectionGrade> studentSectionGrades;
        protected Set<Staff> persons;
        protected String sourceSystemId;
        protected Integer numberOfTerms;
        protected Map<String, ArrayList<Long>> expression;

        public SectionBuilder(){
            enrolledStudents = Lists.newArrayList();
            assignments = Lists.newArrayList();
            studentSectionGrades = Lists.newArrayList();
            persons = Sets.newHashSet();
        }

        public SectionBuilder withNumberOfTerms(final Integer numberOfTerms){
            this.numberOfTerms = numberOfTerms;
            return this;
        }

        public SectionBuilder withStartDate(final LocalDate startDate){
            this.startDate = startDate;
            return this;
        }

        public SectionBuilder withEndDate(final LocalDate endDate){
            this.endDate = endDate;
            return this;
        }

        public SectionBuilder withRoom(final String room){
            this.room = room;
            return this;
        }

        public SectionBuilder withGradeFormula(final GradeFormula formula){
            this.gradeFormula = formula;
            return this;
        }
        
        public SectionBuilder withTerm(final Term term){
            this.term = term;
            return this;
        }

        public SectionBuilder withCourse(final Course course){
            this.course = course;
            return this;
        }

        public SectionBuilder withEnrolledStudent(final Student student){
            enrolledStudents.add(student);
            return this;
        }

        public SectionBuilder withEnrolledStudents(final List<Student> students){
            enrolledStudents.addAll(students);
            return this;
        }

        public SectionBuilder withAssignment(final Assignment assignment){
            assignments.add(assignment);
            return this;
        }

        public SectionBuilder withAssignments(final List<Assignment> assignments){
            this.assignments.addAll(assignments);
            return this;
        }

        public SectionBuilder withStudentSectionGrade(final StudentSectionGrade studentSectionGrade){
            studentSectionGrades.add(studentSectionGrade);
            return this;
        }

        public SectionBuilder withStudentSectionGrades(final List<StudentSectionGrade> studentSectionGrades){
            this.studentSectionGrades.addAll(studentSectionGrades);
            return this;
        }

        public SectionBuilder withPerson(final Staff person){
            persons.add(person);
            return this;
        }

        public SectionBuilder withPersons(final Set<Staff> persons){
            this.persons.addAll(persons);
            return this;
        }

        public SectionBuilder withSourceSystemId(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return this;
        }

        public SectionBuilder withExpression(final Map<String, ArrayList<Long>> expression) {
            this.expression = expression;
            return this;
        }

        public Section build(){
            Section section = super.build();
            section.setStartDate(startDate);
            section.setEndDate(endDate);
            section.setRoom(room);
            section.setGradeFormula(gradeFormula);
            section.setNumberOfTerms(numberOfTerms);
            section.setTerm(term);
            section.setCourse(course);
            section.setEnrolledStudents(enrolledStudents);
            section.setAssignments(assignments);
            section.setTeachers(persons);
            section.setSourceSystemId(sourceSystemId);
            section.setExpression(expression);

            return section;
        }

        @Override
        protected SectionBuilder me() {
            return this;
        }

        @Override
        public Section getInstance() {
            return new Section();
        }
    }

}
