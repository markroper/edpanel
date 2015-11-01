package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.mysql.mapper.PrepScoreMapper;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.PrepScore;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.user.Student;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
public class StudentJdbc extends BaseJdbc implements StudentPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private AuthorityPersistence authorityPersistence;
    
    public StudentJdbc() {
    }

    public StudentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Student> selectAll(Long schoolId) {
        if(null != schoolId) {
            String sql = "FROM student s WHERE s.currentSchoolId = :schoolId";
            return (List<Student>) hibernateTemplate.findByNamedParam(sql, "schoolId", schoolId);
        } else {
            return hibernateTemplate.loadAll(Student.class);
        }
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
    public List<PrepScore> selectStudentPrepScore(long studentId, Date allPrepScoresSince) {
        Map<String, Object> params = new HashMap<>();
        
        // define 'week' buckets -- each eligible prep score contributor (i.e. behavior event) will end up in one of these buckets
        // each date is a saturday that represents the entire following week (through to friday)
        Date[] allWeeks = getSaturdayDatesForAllWeeksSince(allPrepScoresSince);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select point_value, CASE ");
        for (Date week : allWeeks) {
            // need the start date (saturday)...
            String saturdayDateString = dateFormatter.format(week);

            // and the end date (friday)
            Calendar cal = Calendar.getInstance();
            cal.setTime(week);
            // since we know the incoming date is a saturday, this always gets us the following friday
            cal.add(Calendar.DAY_OF_MONTH, 6);
            String fridayDateString = dateFormatter.format(cal.getTime());

            //query for all records within this week, and bucket them appropriately
            queryBuilder.append(" WHEN " + HibernateConsts.BEHAVIOR_DATE + " >= '" + saturdayDateString 
                    + "' AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + fridayDateString 
                    + "' THEN '" + saturdayDateString + "'");
        }

        queryBuilder.append(" END as " + HibernateConsts.BEHAVIOR_DATE);
        
        queryBuilder.append(" from behavior");
        System.out.println("Build query: " + queryBuilder.toString());
        List<PrepScore> prepScores = jdbcTemplate.query(
                queryBuilder.toString(),
                params,
                new PrepScoreMapper()
        );
        System.out.println("Got prepscores back...");
        for (PrepScore prepScore : prepScores) {
            System.out.println(prepScore);
        }
        return prepScores;
    }

    // constructs an array containing zero or more dates.
    // each of these dates will occur on a saturday at noon, which for our purposes represents a week (saturday - friday)
    // if the date provided is not a saturday at noon, the most recent saturday BEFORE the specified date will be used
    protected Date[] getSaturdayDatesForAllWeeksSince(Date allPrepScoresSince) {
        // if date is saturday
        // use date
        Date currentSaturday = null;
        if (isSaturdayDate(allPrepScoresSince)) {
            currentSaturday = allPrepScoresSince;
        } else {
            // otherwise, get saturday-date from non-saturday date
            currentSaturday = getRecentSaturdayForDate(allPrepScoresSince);
        }

        ArrayList<Date> validSaturdayDates = new ArrayList<>();

        // if the date is in the past, save it and increment a week
        // until we pass by the present
        while (!currentSaturday.after(Calendar.getInstance().getTime())) {
            validSaturdayDates.add(currentSaturday);
            Calendar c = Calendar.getInstance();
            c.setTime(currentSaturday);
            c.add(Calendar.DAY_OF_MONTH, 7);
            currentSaturday = c.getTime();
        }
        
        return validSaturdayDates.toArray(new Date[0]);
    }

    private Date getRecentSaturdayForDate(Date allPrepScoresSince) {
        if (allPrepScoresSince == null) { return null; }
        if (isSaturdayDate(allPrepScoresSince)) { return allPrepScoresSince; } 
        
        Calendar c = Calendar.getInstance();
        c.setTime(allPrepScoresSince);
        while (!(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        return c.getTime();
    }

    private boolean isSaturdayDate(Date allPrepScoresSince) {
        Calendar c = Calendar.getInstance();
        c.setTime(allPrepScoresSince);
        return (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Student selectBySsid(Long ssid) {
        String query = "select s from student s where s.sourceSystemId = :ssid";
        List<Student> students = (List<Student>) hibernateTemplate.findByNamedParam(query, "ssid", ssid.toString());
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
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.STUDENT);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
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

    public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
        this.authorityPersistence = authorityPersistence;
    }
}
