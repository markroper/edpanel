package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.scholarscore.api.persistence.mysql.DbMappings;
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
    @SuppressWarnings("unchecked")
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        String sql = "FROM studentSectionGrade ssg WHERE ssg.section.id = (?)";

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
        Student out = hibernateTemplate.merge(student);
        student.setId(out.getId());
        return out.getId();
    }

    @Override
    public Long replaceStudent(long studentId, Student student) {
        hibernateTemplate.merge(student);
        return studentId;
    }

    @Override
    public Long delete(long studentId) {
        Student student = hibernateTemplate.get(Student.class, studentId);
        if (null != student) {
            hibernateTemplate.delete(student);
        }
        return studentId;
    }
}
