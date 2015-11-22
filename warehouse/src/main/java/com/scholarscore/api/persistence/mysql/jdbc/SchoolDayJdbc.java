package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.SchoolDayPersistence;
import com.scholarscore.api.persistence.SchoolPersistence;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.attendance.SchoolDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Transactional
public class SchoolDayJdbc implements SchoolDayPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    private SchoolPersistence schoolPersistence;
    
    public SchoolDayJdbc(){
    }
    
    public SchoolDayJdbc(HibernateTemplate t) {
        this.hibernateTemplate = t;
    }
    
    @Override
    public Long insertSchoolDay(long schoolId, SchoolDay schoolDay) {
        if(null == schoolDay.getSchool() || !schoolDay.getSchool().getId().equals(schoolId)) {
            schoolDay.getSchool().setId(schoolId);
        }
        SchoolDay output = hibernateTemplate.merge(schoolDay);
        return output.getId();
    }

    @Override
    public List<Long> insertSchoolDays(long schoolId, List<SchoolDay> schoolDays) {
        int i = 0;
        List<Long> ids = new ArrayList<>();
        for(SchoolDay day : schoolDays) {
            hibernateTemplate.save(day);
            ids.add(day.getId());
            //Release newly created entities from hibernates session im-memory storage
            if(i % 20 == 0) {
                hibernateTemplate.flush();
                hibernateTemplate.clear();
            }
            i++;
        }
        return ids;
    }

    @Override
    public SchoolDay select(long schoolId, long schoolDayId) {
        return hibernateTemplate.get(SchoolDay.class, schoolDayId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<SchoolDay> selectAllSchoolDays(long schoolId) {
        return (Collection<SchoolDay>)hibernateTemplate.findByNamedParam(
                "from school_day d where d.school.id = :id", "id", schoolId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<SchoolDay> selectAllSchoolDaysInYear(long schoolId, long schoolYearId) {
        String[] paramNames = new String[] { "id", "startDate", "endDate"}; 
        School s = schoolPersistence.selectSchool(schoolId);
        Date startDate = null;
        Date endDate = null;
        if(null != s.getYears()) {
            for(SchoolYear year: s.getYears()) {
                if(year.getId().equals(schoolYearId)) {
                    startDate = year.getStartDate();
                    endDate = year.getEndDate();
                    break;
                }
            }
        }
        Object[] paramValues = new Object[]{ new Long(schoolId), startDate, endDate };
        return (Collection<SchoolDay>)hibernateTemplate.findByNamedParam(
                "from school_day d where d.school.id = :id and d.date >= :startDate and d.date <= :endDate", 
                paramNames, 
                paramValues);
    }

    @Override
    public Long delete(long schoolId, long schoolDayId) {
        SchoolDay day = select(schoolId, schoolDayId);
        if (null != day) {
            hibernateTemplate.delete(day);
        }
        return schoolDayId;
    }

    @Override
    public Long update(long schoolId, long schoolDayId, SchoolDay day) {
        hibernateTemplate.update(day);
        return schoolDayId;
    }

    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }
    
    public void setHibernateTemplate(HibernateTemplate t) {
        this.hibernateTemplate = t;
    }
}
