package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.StudentSectionGradePersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentSectionGradeMapper;
import com.scholarscore.models.StudentSectionGrade;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class StudentSectionGradeJdbc implements StudentSectionGradePersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

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
        StudentSectionGrade out = hibernateTemplate.merge(entity);
        return out.getId();
    }

    @Override
    public Long update(long sectionId, long studentId, StudentSectionGrade entity) {

        StudentSectionGrade update = select(sectionId, studentId);
        if (null != update) {
            update.setStudent(entity.getStudent());
            update.setGrade(entity.getGrade());
            update.setComplete(entity.getComplete());
            update.setSection(entity.getSection());
            hibernateTemplate.merge(update);
        }
        return update.getId();
    }

    @Override
    public Long delete(long sectionId, long studentId) {
        StudentSectionGrade toDelete = select(sectionId, studentId);
        if (null != toDelete) {
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
}
