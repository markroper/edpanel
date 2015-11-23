package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.SectionPersistence;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class SectionJdbc implements SectionPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private EntityPersistence<Term> termEntityPersistence;

    public SectionJdbc() {
    }

    public SectionJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Section> selectAll(long termId) {
        return (Collection<Section>)hibernateTemplate.findByNamedParam("from section s where s.term.id = :id", "id", termId);
    }

    @Override
    public Section select(long termId, long sectionId) {
        return hibernateTemplate.get(Section.class, sectionId);
    }

    @Override
    public Long insert(long termId, Section entity) {
        injectTerm(termId, entity);
        Section result = hibernateTemplate.merge(entity);
        return result.getId();
    }

    private void injectTerm(long termId, Section entity) {
        if (null == entity.getTerm()) {
            entity.setTerm(termEntityPersistence.select(0L, termId));
        }
    }

    @Override
    public Long update(long termId, long sectionId, Section entity) {
        injectTerm(termId, entity);
        if (null == entity.getId()) {
            entity.setId(sectionId);
        }
        hibernateTemplate.merge(entity);
        return sectionId;
    }

    @Override
    public Long delete(long id) {
        Section term = select(0L, id);
        if (null != term) {
            hibernateTemplate.delete(term);
        }
        return id;
    }

    public EntityPersistence<Term> getTermEntityPersistence() {
        return termEntityPersistence;
    }

    public void setTermEntityPersistence(EntityPersistence<Term> termEntityPersistence) {
        this.termEntityPersistence = termEntityPersistence;
    }

    @Override
    public Collection<Section> selectAllSectionForStudent(long termId, long studentId) {
        String[] params = new String[]{"termId", "studentId"};
        Object[] paramValues = new Object[]{ new Long(termId), new Long(studentId) };


        List<?> objects = hibernateTemplate.findByNamedParam(
                StudentSectionGradeJdbc.SSG_HQL_BASE +
                "where ssg.section.term.id = :termId and ssg.student.id = :studentId",
                params, 
                paramValues);
        ArrayList<Section> sectionList = new ArrayList<>();
        for(Object obj : objects) {
            sectionList.add(((StudentSectionGrade)obj).getSection());
        }
        return sectionList;
        
    }

    @Override
    public Collection<Section> selectAllSectionForTeacher(
            long termId, long teacherId) {
        String[] params = new String[]{"termId", "teacherId"};
        Object[] paramValues = new Object[]{ new Long(termId), new Long(teacherId) }; 
        List<?> objects = hibernateTemplate.findByNamedParam(
                "from section s join s.teachers ts where s.term.id = :termId and ts.id = :teacherId", 
                params, 
                paramValues);
        ArrayList<Section> sectionList = new ArrayList<>();
        for(Object obj : objects) {
            Object[] coll = (Object[]) obj;
            sectionList.add((Section)coll[0]);    
        }
        return sectionList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Section> selectAllInSchool(long schoolId) {
        String[] params = new String[]{"schoolId"};
        Object[] paramValues = new Object[]{ new Long(schoolId) };
        List<Section> objects = (List<Section>)hibernateTemplate.findByNamedParam(
                "from section s where s.term.schoolYear.school.id = :schoolId",
                params,
                paramValues);
        return objects;
    }
}
