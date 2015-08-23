package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentSectionGrade;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;
import javax.transaction.Transactional;

@Transactional
public class StudentJdbc implements StudentPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public StudentJdbc() {
    }

    public StudentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Student> selectAll() {
        return hibernateTemplate.loadAll(Student.class);
    }

    @Override
    /**
     * String SELECT_STUDENTS_IN_SECTION_SQL = SELECT_ALL_STUDENTS_SQL +
     " INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`" +
     " ON `" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`.`" + DbConst.STUD_FK_COL + "` = `" +
     DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` " +
     "WHERE `" + DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL;

     */
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        String sql = "FROM student_section_grade WHERE section_fk = (?)";

        List<StudentSectionGrade> studentSectionGrades = (List<StudentSectionGrade>) hibernateTemplate.find(
                sql, sectionId);
        List<Student> students = new ArrayList<>();
        for (StudentSectionGrade grade : studentSectionGrades) {
            Student student = grade.getStudent();
            students.add(student);
        }
        return students;
    }

    @Override
    public Student select(long studentId) {
        return hibernateTemplate.get(Student.class, studentId);
    }

    @Override
    public Long createStudent(Student student) {
        Long value = (Long) hibernateTemplate.save(student);
        return value;
    }

    @Override
    public Long replaceStudent(long studentId, Student student) {
        hibernateTemplate.update(student);
        return studentId;
    }

    @Override
    public Long delete(long studentId) {
        Student admin = hibernateTemplate.get(Student.class, studentId);
        hibernateTemplate.delete(admin);
        return studentId;
    }
}
