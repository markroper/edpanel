package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AttendancePersistence;
import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.models.School;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Transactional
public class AttendanceJdbc implements AttendancePersistence {
    //force the ugly joins all in one SQL statement via HQL's JOIN FETCH.  This prevents the M * N query problem :)
    private static final String ATTENDANCE_HQL = "from attendance a " +
        "join fetch a.schoolDay d join fetch d.school s left join fetch s.address add " +
        "join fetch a.student st left join fetch st.homeAddress left join fetch st.mailingAddress left join fetch st.contactMethods " +
        "left join fetch a.section sect left join fetch sect.term t left join fetch t.schoolYear y left join fetch y.school sch left join fetch sch.address " +
        "left join fetch sect.course course left join fetch course.school cSch left join fetch cSch.address " +
        "left join fetch sect.teachers teachers left join fetch teachers.contactMethods left join fetch teachers.homeAddress";

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
    public void insertAttendances(Long schoolId, Long studentId, List<Attendance> attendances) {
        int i = 0;
        for(Attendance attendance : attendances) {
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
            hibernateTemplate.save(attendance);
            //Release newly created entities from hibernates session im-memory storage
            if(i % 20 == 0) {
                hibernateTemplate.flush();
                hibernateTemplate.clear();
            }
            i++;
        }
    }

    @Override
    public Attendance select(Long schoolId, Long studentId, Long attendanceId) {
        return hibernateTemplate.get(Attendance.class, attendanceId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAllAttendance(Long schoolId,
                                                      Long studentId) {
        String[] paramNames = new String[] {"studentId" };
        Object[] paramValues = new Object[]{ studentId };
        return (Collection<Attendance>)hibernateTemplate.findByNamedParam(
                ATTENDANCE_HQL + " where a.schoolDay.school.id = :schoolId and a.student.id = :studentId",
                paramNames,
                paramValues);

    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAllDailyAttendance(Long studentId) {
        String[] paramNames = new String[] {"studentId" };
        Object[] paramValues = new Object[]{studentId };
        return (Collection<Attendance>)hibernateTemplate.findByNamedParam(
                ATTENDANCE_HQL + " where a.attendance_type = 'DAILY' and a.student.id = :studentId",
                paramNames,
                paramValues);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Attendance> selectAllAttendance(Long schoolId, List<Long> studentIds, LocalDate start, LocalDate end) {
        String[] paramNames = new String[] { "schoolId", "studentIds", "startDate", "endDate" };
        Object[] paramValues = new Object[]{ schoolId, studentIds, start, end };
        return (Collection<Attendance>)hibernateTemplate.findByNamedParam(
                ATTENDANCE_HQL + " where a.schoolDay.school.id = :schoolId and a.student.id in (:studentIds) and " +
                    "a.schoolDay.date >= :startDate and a.schoolDay.date <= :endDate",
                paramNames,
                paramValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAttendanceForSection(Long studentId, Long sectionId) {
        String[] paramNames = new String[] {"studentId", "sectionId"};
        Object[] paramValues = new Object[]{studentId, sectionId};
        return (Collection<Attendance>)hibernateTemplate.findByNamedParam(
                ATTENDANCE_HQL + " where a.student.id = :studentId" +
                        " and a.section.id = :sectionId",
                paramNames,
                paramValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attendance> selectAllAttendanceForTerm(Long schoolId,
            Long studentId, Long yearId, Long termId) {
        String[] paramNames = new String[] { "schoolId", "studentId", "startDate", "endDate" };
        LocalDate startDate = null;
        LocalDate endDate = null;
        Term t = termPersistence.select(yearId, termId);
        if(null != t) {
            startDate = t.getStartDate();
            endDate = t.getEndDate();
        }
        Object[] paramValues = new Object[]{ schoolId, studentId, startDate, endDate };
        return (Collection<Attendance>) hibernateTemplate.findByNamedParam(
                ATTENDANCE_HQL + " where a.schoolDay.school.id = :schoolId and a.student.id = :studentId "
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

    @Override
    public void update(Long schoolId, Long studentId, Long attendanceId, Attendance a) {
        hibernateTemplate.update(a);
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }   
    
    public void setTermPersistence(EntityPersistence<Term> termPersistence) {
        this.termPersistence = termPersistence;
    }
}
