package com.scholarscore.api.persistence.mysql.jdbc;

import com.mysql.jdbc.log.LogFactory;
import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.mysql.mapper.PrepScoreMapper;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.PrepScore;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelDateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
public class StudentJdbc extends BaseJdbc implements StudentPersistence {

    private final static Logger logger = LoggerFactory.getLogger(StudentJdbc.class);
    
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

    // TODO Jordan: add sensible default for allPrepScoresSince? (or otherwise handle null date)
    // (or add in controller?) 
    @Override
    public List<PrepScore> selectStudentPrepScore(Long[] studentIds, Date startDate, Date endDate) {
        // define 'week' buckets -- each eligible prep score contributor (i.e. behavior event) will end up in one of these buckets
        // each date is a saturday that represents the entire following week (through to friday)
        Date[] allWeeks = EdPanelDateUtil.getSaturdayDatesForWeeksBetween(startDate, endDate);

        // TODO Jordan: SDF is not threadsafe but costly to create. use threadlocal or something
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        // build the CASES fragment of the query to bucket the behavior events by week
        String casesFrag = buildCaseFrag(allWeeks);
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        queryBuilder.append(HibernateConsts.STUDENT_FK + ", ");
        queryBuilder.append(PrepScore.INITIAL_PREP_SCORE + " + sum(" + HibernateConsts.BEHAVIOR_POINT_VALUE + ")" 
                        + " as " + HibernateConsts.BEHAVIOR_POINT_VALUE + ", ");
        // include the CASE statements assembled earlier
        queryBuilder.append(casesFrag);
        queryBuilder.append(" FROM " + HibernateConsts.BEHAVIOR_TABLE);

        // filter by date range
        List<Date> allWeeksList = Arrays.asList(allWeeks);
        Collections.sort(allWeeksList);
        String firstSaturdayString = dateFormatter.format(allWeeksList.get(0)); // first saturday
        Date latestSaturday = allWeeksList.get(allWeeksList.size() - 1);
        String lastFridayString = dateFormatter.format(DateUtils.addDays(latestSaturday, 6)); // add 6 days to go from sat -> fri
        queryBuilder.append(" WHERE " + HibernateConsts.BEHAVIOR_DATE + " >= '" + firstSaturdayString + "'" 
                        + " AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + lastFridayString + "'");
        
        // ... and filter by student
        queryBuilder.append(" AND (" + buildStudentWhereClauseFrag(studentIds) + ")");
        
        // group by student+date(s)
        queryBuilder.append(" GROUP BY " + HibernateConsts.STUDENT_FK + ", " + HibernateConsts.START_DATE + ", " + HibernateConsts.END_DATE);
        System.out.println("Built query for prepscore: " + queryBuilder.toString());

        // run query 
        List<PrepScore> prepScores = jdbcTemplate.query(queryBuilder.toString(), new HashMap<>(), new PrepScoreMapper());
        return prepScores;
    }

    private String buildCaseFrag(Date[] allWeeks) {
        // here, two CASE statements are used to bucket behavior events to specific weeks.
        // for each week, one case will return the saturday before the behavioral event, if not already a saturday
        // the other case will return the friday after the event, if not already a friday
        StringBuilder caseOneBuilder = new StringBuilder();
        StringBuilder caseTwoBuilder = new StringBuilder();

        // TODO Jordan: SDF is not threadsafe but costly to create. use threadlocal or something
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        caseOneBuilder.append("CASE ");
        caseTwoBuilder.append("CASE ");
        for (Date week : allWeeks) {
            // need the start date (saturday)...
            String saturdayDateString = dateFormatter.format(week);
            // and the end date (friday)
            String fridayDateString = dateFormatter.format(DateUtils.addDays(week, 6));

            //query for all records within this week, and bucket them appropriately
            // start_date column will be the saturday (beginning of the week of the prepscore in question)
            caseOneBuilder.append(" WHEN " + HibernateConsts.BEHAVIOR_DATE + " >= '" + saturdayDateString
                    + "' AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + fridayDateString
                    + "' THEN '" + saturdayDateString + "'");
            // end_date column will be the friday (ending of the week of the prepscore in question)
            caseTwoBuilder.append(" WHEN " + HibernateConsts.BEHAVIOR_DATE + " >= '" + saturdayDateString
                    + "' AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + fridayDateString
                    + "' THEN '" + fridayDateString + "'");

        }
        caseOneBuilder.append(" END as " + HibernateConsts.START_DATE);
        caseTwoBuilder.append(" END as " + HibernateConsts.END_DATE);
        
        return caseOneBuilder.toString() + ", " + caseTwoBuilder.toString();
    }

    private String buildStudentWhereClauseFrag(Long[] studentIds) {
        StringBuilder sb = new StringBuilder();
        boolean oneAdded = false;
        for (long studentId : studentIds) {
            if (oneAdded) {
                sb.append(" OR ");
            } else {
                oneAdded = true;
            }
            sb.append(HibernateConsts.STUDENT_FK + " = '" + studentId + "'");
        }
        return sb.toString();
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
