package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AttendancePersistence;
import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.models.School;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;

@Transactional
public class AttendanceJdbc implements AttendancePersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    private EntityPersistence<Term> termPersistence;

    public AttendanceJdbc() {
    }

    public AttendanceJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Long insertAttendance(Long schoolId, Long studentId,
            Attendance attendance) {
        //Setup the school & schoolId, if needed
        if(null == attendance.getSchoolDay()) {
            attendance.setSchoolDay(new SchoolDay());
        }
        if(null == attendance.getSchoolDay().getSchool()) {
            attendance.getSchoolDay().setSchool(new School());
        }
        attendance.getSchoolDay().getSchool().setId(schoolId);
        //Setup the student, if needed
        if(null == attendance.getStudent()) {
            attendance.setStudent(new Student());
        }
        attendance.getStudent().setId(studentId);
        Attendance a = this.hibernateTemplate.merge(attendance);
        return a.getId();
    }

    @Override
    public Attendance select(Long schoolId, Long studentId, Long attendanceId) {
        return hibernateTemplate.get(Attendance.class, attendanceId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAllAttendance(Long schoolId,
            Long studentId) {
        String[] paramNames = new String[] { "schoolId", "studentId" }; 
        Object[] paramValues = new Object[]{ schoolId, studentId };
        return (Collection<Attendance>)hibernateTemplate.findByNamedParam(
                "from attendance a where a.schoolDay.school.id = :schoolId and a.student.id = :studentId", 
                paramNames, 
                paramValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAllAttendanceForTerm(Long schoolId,
            Long studentId, Long yearId, Long termId) {
        String[] paramNames = new String[] { "schoolId", "studentId", "startDate", "endDate" }; 
        Date startDate = null;
        Date endDate = null;
        Term t = termPersistence.select(yearId, termId);
        if(null != t) {
            startDate = t.getStartDate();
            endDate = t.getEndDate();
        }
        Object[] paramValues = new Object[]{ schoolId, studentId, startDate, endDate };
        return (Collection<Attendance>) hibernateTemplate.findByNamedParam(
                "from attendance a where a.schoolDay.school.id = :schoolId and a.student.id = :studentId "
                + "and a.schoolDay.date >= :startDate and a.schoolDay.date <= :endDate", 
                paramNames, 
                paramValues);
    }

    @Override
    public Long delete(Long schoolId, Long studentId, Long attendanceId) {
        Attendance day = select(schoolId, studentId, attendanceId);
        if (null != day) {
            hibernateTemplate.delete(day);
        }
        return attendanceId;
    }
    
    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }   
    
    public void setTermPersistence(EntityPersistence<Term> termPersistence) {
        this.termPersistence = termPersistence;
    }
}
