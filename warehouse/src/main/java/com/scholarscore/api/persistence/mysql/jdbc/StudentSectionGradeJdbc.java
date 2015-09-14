package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.List;

import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.models.Section;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.StudentSectionGradePersistence;
import com.scholarscore.models.StudentSectionGrade;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class StudentSectionGradeJdbc implements StudentSectionGradePersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private StudentPersistence studentPersistence;
    private EntityPersistence<Section> sectionPersistence;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentSectionGrade> selectAll(long sectionId) {
        return (Collection<StudentSectionGrade>)hibernateTemplate.findByNamedParam("from studentSectionGrade ssg where ssg.section.id = :id", "id", sectionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentSectionGrade> selectAllByStudent(long studentId) {
        return (Collection<StudentSectionGrade>)hibernateTemplate.findByNamedParam("from studentSectionGrade ssg where ssg.student.id = :id", "id", studentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StudentSectionGrade select(long sectionId, long studentId) {
        List<StudentSectionGrade> gradeList = (List<StudentSectionGrade>) hibernateTemplate.findByNamedParam("from studentSectionGrade ssg where ssg.student.id = " + String.valueOf(studentId) +
                " and ssg.section.id = :sectionId", "sectionId", sectionId);
        if (null != gradeList && gradeList.size() > 0) {
            return gradeList.get(0);
        }
        return null;
    }

    @Override
    public Long insert(long sectionId, long studentId, StudentSectionGrade entity) {
        injectStudent(studentId, entity);
        injectSection(sectionId, entity);
        StudentSectionGrade out = hibernateTemplate.merge(entity);
        return out.getId();
    }

    private void injectSection(long sectionId, StudentSectionGrade entity) {
        if (null == entity.getSection()) {
            entity.setSection(sectionPersistence.select(0L, sectionId));
        }
    }

    private void injectStudent(long studentId, StudentSectionGrade entity) {
        if (null == entity.getStudent()) {
            entity.setStudent(studentPersistence.select(studentId));
        }
    }

    @Override
    public Long update(long sectionId, long studentId, StudentSectionGrade entity) {

        StudentSectionGrade update = select(sectionId, studentId);
        if (null != update) {
            update.setStudent(entity.getStudent());
            update.setGrade(entity.getGrade());
            update.setComplete(entity.getComplete());
            update.setSection(entity.getSection());
            injectStudent(studentId, update);
            injectSection(sectionId, update);
            hibernateTemplate.merge(update);
        }
        return update.getId();
    }

    @Override
    public Long delete(long sectionId, long studentId) {
        StudentSectionGrade toDelete = select(sectionId, studentId);
        if (null != toDelete) {
            toDelete.getSection().getStudentSectionGrades().remove(toDelete);
            hibernateTemplate.delete(toDelete);
        }
        return toDelete.getId();
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }

    public void setSectionPersistence(EntityPersistence<Section> sectionPersistence) {
        this.sectionPersistence = sectionPersistence;
    }
}
