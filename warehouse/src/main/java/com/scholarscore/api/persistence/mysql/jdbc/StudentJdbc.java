package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.user.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    @SuppressWarnings("unchecked")
    public Student select(String username) {
        String query = "select s from student s join s.user u where u.username = :username";
        List<Student> students = (List<Student>) hibernateTemplate.findByNamedParam(query, "username", username);
        if (students.size() == 1) {
            return students.get(0);
        }
        return null;
    }

    @Override
    public Long createStudent(Student student) {
        assignDefaults(student);
        Student out = hibernateTemplate.merge(student);
        student.setId(out.getId());
        return out.getId();
    }

    @Override
    public Long replaceStudent(long studentId, Student student) {
        assignDefaults(student);
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
    
    private static void assignDefaults(Student student) {
        if(null == student.getPassword()) {
            student.setPassword(UUID.randomUUID().toString());
        }
        if(null == student.getUsername()) {
            student.setUsername(UUID.randomUUID().toString());
        }
    }
}
