package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.mysql.jdbc.StudentSectionGradeJdbc;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.user.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class StudentJdbc extends UserBaseJdbc implements StudentPersistence {
    private static final String STUDENT_HQL =
            "FROM student s " +
            "left join fetch s.homeAddress " +
            "left join fetch s.mailingAddress " +
            "left join fetch s.contactMethods";
    private static final String ACTIVE_STUDENTS_CLAUSE =
            " and s.withdrawalDate is null";// and s.enrollStatus = '"  + EnrollStatus.CURRENTLY_ENROLLED.name() + "'";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private AuthorityPersistence authorityPersistence;
    
    public StudentJdbc() { }

    public StudentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Student> selectAll(Long schoolId, Boolean activeStudents) {
        String whereClause = " WHERE s.currentSchoolId = :schoolId";
        if(null == activeStudents || !activeStudents) {
            whereClause += " and s.withdrawalDate is not null";
            //or s.enrollStatus != '" + EnrollStatus.CURRENTLY_ENROLLED.name() + "'";
        } else {
            whereClause += ACTIVE_STUDENTS_CLAUSE;
        }
        if(null != schoolId) {
            String sql = STUDENT_HQL + whereClause;
            return (List<Student>) hibernateTemplate.findByNamedParam(sql, "schoolId", schoolId);
        } else {
            return hibernateTemplate.loadAll(Student.class);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Student> selectAll(Long schoolId, FilteredStudents students, Boolean activeStudents) {
        String[] params;
        Object[] paramValues;
        List<String> paramsList = new ArrayList<>();
        List<Object> paramValuesList = new ArrayList<>();
        String studentWhereClause = "";
        if(null != schoolId) {
            paramsList.add("schoolId");
            paramValuesList.add(schoolId);
            studentWhereClause += " and s.currentSchoolId = :schoolId";
        }
        if(null != activeStudents || !activeStudents) {
            studentWhereClause += " and s.withdrawalDate is not null";
            //or s.enrollStatus != '"+ EnrollStatus.CURRENTLY_ENROLLED.name() + "'";
        } else {
            studentWhereClause += ACTIVE_STUDENTS_CLAUSE;
        }
        if(null != students) {
            if(null != students.getGender()) {
                paramsList.add("gender");
                paramValuesList.add(students.getGender());
                studentWhereClause += " and s.gender = :gender";
            }
            if(null != students.getFederalRaces() && !students.getFederalRaces().isEmpty()) {
                paramsList.add("federalRaces");
                paramValuesList.add(students.getFederalRaces());
                studentWhereClause += " and s.federalRace in (:federalRaces)";
            }
            if(null != students.getFederalEthnicities() && !students.getFederalEthnicities().isEmpty()) {
                paramsList.add("federalEthnicities");
                paramValuesList.add(students.getFederalEthnicities());
                studentWhereClause += " and s.federalEthnicity in (:federalEthnicities)";
            }
            if(null != students.getProjectedGraduationYears() && !students.getProjectedGraduationYears().isEmpty()) {
                paramsList.add("projectedGraduationYears");
                paramValuesList.add(students.getProjectedGraduationYears());
                studentWhereClause += " and s.projectedGraduationYear in (:projectedGraduationYears)";
            }
            if(null != students.getDistrictEntryYears() && !students.getDistrictEntryYears().isEmpty()) {
                paramsList.add("districtEntryYears");
                paramValuesList.add(students.getDistrictEntryYears());
                studentWhereClause += " and s.districtEntryYear in (:districtEntryYears)";
            }
            if(null != students.getBirthYears() && !students.getBirthYears().isEmpty()) {
                paramsList.add("birthYears");
                paramValuesList.add(students.getBirthYears());
                studentWhereClause += " and s.birthYear in (:birthYears)";
            }
            //TODO: ELL & Sped flags, when they're in...
        }
        params = new String[paramsList.size()];
        paramsList.toArray(params);
        paramValues = new Object[paramValuesList.size()];
        paramValuesList.toArray(paramValues);
        studentWhereClause = " where " + studentWhereClause.substring(5);

        List<Student> studs = (List<Student>)hibernateTemplate.findByNamedParam(
                STUDENT_HQL + studentWhereClause,
                params,
                paramValues);
        return studs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        String sql = StudentSectionGradeJdbc.SSG_HQL_BASE +  " WHERE st.withdrawalDate is null and ssg.section.id = :sectionId";

        List<StudentSectionGrade> studentSectionGrades = (List<StudentSectionGrade>) hibernateTemplate.findByNamedParam(
                sql, "sectionId", sectionId);
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
        transformUserValues(student, null);
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
        transformUserValues(student, select(studentId));
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

    public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
        this.authorityPersistence = authorityPersistence;
    }
}
