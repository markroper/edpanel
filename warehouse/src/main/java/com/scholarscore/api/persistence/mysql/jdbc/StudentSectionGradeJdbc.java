package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.Section;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Transactional
public class StudentSectionGradeJdbc implements StudentSectionGradePersistence {

    public static final String SSG_HQL_BASE =  "select ssg from " + HibernateConsts.STUDENT_SECTION_GRADE_TABLE + " ssg " +
            "join fetch ssg.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
            "left join fetch st.contactMethods " +
            "left join fetch ssg.overallGrade " +
            "join fetch ssg.section s join fetch s.course c join fetch c.school " +
            "join fetch s.term t join fetch t.schoolYear y join fetch y.school " +
            "left join fetch s.teachers te left join fetch te.homeAddress left join fetch te.contactMethods ";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private StudentPersistence studentPersistence;
    private EntityPersistence<Section> sectionPersistence;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentSectionGrade> selectAll(long sectionId) {
        return (Collection<StudentSectionGrade>)hibernateTemplate.findByNamedParam("from " + HibernateConsts.STUDENT_SECTION_GRADE_TABLE + " ssg where ssg.section.id = :id", "id", sectionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentSectionGrade> selectAllByTerm(long termId, long schoolId) {
        return (Collection<StudentSectionGrade>)hibernateTemplate.findByNamedParam(
                SSG_HQL_BASE +
                        "where y.school.id = :schoolId", "schoolId", schoolId
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentSectionGrade> selectAllByStudent(long studentId) {
        return (Collection<StudentSectionGrade>)hibernateTemplate.findByNamedParam(
                SSG_HQL_BASE +
                "where ssg.student.id = :id", "id", studentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StudentSectionGrade select(long sectionId, long studentId) {
        List<StudentSectionGrade> gradeList = (List<StudentSectionGrade>) hibernateTemplate.findByNamedParam(
                SSG_HQL_BASE +
                " where ssg.student.id = " + String.valueOf(studentId) +
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
        Serializable out = hibernateTemplate.save(entity);
        return new Long(out.toString());
    }

    @Override
    public void insertAll(long sectionId, List<StudentSectionGrade> entities) {
        int i = 0;
        for(StudentSectionGrade ssg : entities) {
            hibernateTemplate.save(ssg);
            //Release newly created entities from hibernates session im-memory storage
            if(i % 20 == 0) {
                hibernateTemplate.flush();
                hibernateTemplate.clear();
            }
            i++;
        }
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
        injectStudent(studentId, entity);
        injectSection(sectionId, entity);
        hibernateTemplate.update(entity);
        return entity.getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(long sectionId, long studentId) {
        StudentSectionGrade toDelete = select(sectionId, studentId);
        if (null != toDelete) {
            hibernateTemplate.delete(toDelete);
        }
        String[] params = new String[]{ "sectionId", "studentId" };
        Object[] paramValues = new Object[]{ sectionId, studentId };
        List<SectionGrade> sgs = (List<SectionGrade>)hibernateTemplate.findByNamedParam(
                "select sg from " + HibernateConsts.SECTION_GRADE_TABLE +
                " sg where sg.sectionFk = :sectionId and sg.studentFk = :studentId", params, paramValues);
        if(null != sgs) {
            hibernateTemplate.deleteAll(sgs);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SectionGrade> getSectionGradeOverTime(long studentId, long sectionId, LocalDate start, LocalDate end) {
        List<String> paramList = Arrays.asList("sectionId", "studentId");
        List<Object> paramValuesList = Arrays.asList(sectionId, studentId);
        String hql = "select sg from " + HibernateConsts.SECTION_GRADE_TABLE +
                " sg where sg.sectionFk = :sectionId and sg.studentFk = :studentId";
        if(null != start) {
            paramList.add("startDate");
            paramValuesList.add(start);
            hql += " and sg.date >= :startDate";
        }
        if(null != end) {
            paramList.add("endDate");
            paramValuesList.add(end);
            hql += " and sg.date <= :endDate";
        }
        String[] params = new String[paramList.size()];
        params = paramList.toArray(params);
        Object[] values = new Object[paramValuesList.size()];
        values = paramValuesList.toArray(values);
        List<SectionGrade> sgs = (List<SectionGrade>)hibernateTemplate.findByNamedParam(hql, params, values);
        return sgs;
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
