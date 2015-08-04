package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentMapper;
import com.scholarscore.models.Address;
import com.scholarscore.models.Student;

public class StudentJdbc extends EnhancedBaseJdbc<Student> implements StudentPersistence {
    private static String INSERT_STUDENT_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "` " +
            "(" + DbConst.STUDENT_NAME_COL + ", " +
                DbConst.STUDENT_SOURCE_SYSTEM_ID + ", " +
                DbConst.STUDENT_MAILING_STREET_COL + ", " +
                DbConst.STUDENT_MAILING_CITY_COL + ", " +
                DbConst.STUDENT_MAILING_STATE_COL + ", " +
                DbConst.STUDENT_MAILING_POSTAL_COL + ", " +
                DbConst.STUDENT_HOME_STREET_COL + ", " +
                DbConst.STUDENT_HOME_CITY_COL + ", " +
                DbConst.STUDENT_HOME_STATE_COL + ", " +
                DbConst.STUDENT_HOME_POSTAL_COL + ", " +
                DbConst.STUDENT_GENDER_COL + ", " +
                DbConst.STUDENT_BIRTH_DATE_COL + ", " +
                DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL + ", " +
                DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL + ", " +
                DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL + ", " +
                DbConst.STUDENT_RACE_COL + ", " +
                DbConst.SCHOOL_FK_COL + ", " +
                DbConst.STUDENT_ETHNICITY_COL +
            ") VALUES (" + 
                ":" + DbConst.STUDENT_NAME_COL + ", " +
                ":" + DbConst.STUDENT_SOURCE_SYSTEM_ID + ", " +
                ":" + DbConst.STUDENT_MAILING_STREET_COL + ", " +
                ":" + DbConst.STUDENT_MAILING_CITY_COL + ", " +
                ":" + DbConst.STUDENT_MAILING_STATE_COL + ", " +
                ":" + DbConst.STUDENT_MAILING_POSTAL_COL + ", " +
                ":" + DbConst.STUDENT_HOME_STREET_COL + ", " +
                ":" + DbConst.STUDENT_HOME_CITY_COL + ", " +
                ":" + DbConst.STUDENT_HOME_STATE_COL + ", " +
                ":" + DbConst.STUDENT_HOME_POSTAL_COL + ", " +
                ":" + DbConst.STUDENT_GENDER_COL + ", " +
                ":" + DbConst.STUDENT_BIRTH_DATE_COL + ", " +
                ":" + DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL + ", " +
                ":" + DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL + ", " +
                ":" + DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL + ", " +
                ":" + DbConst.STUDENT_RACE_COL + ", " +
                ":" + DbConst.SCHOOL_FK_COL + ", " +
                ":" + DbConst.STUDENT_ETHNICITY_COL +  
                ")";   

    private static String UPDATE_STUDENT_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.STUDENT_TABLE + "` " + 
            "SET `" + DbConst.STUDENT_NAME_COL + "`= :" + DbConst.STUDENT_NAME_COL + ", `" + 
                 DbConst.STUDENT_SOURCE_SYSTEM_ID + "`= :" + DbConst.STUDENT_SOURCE_SYSTEM_ID + ", `" +
                 DbConst.STUDENT_MAILING_STREET_COL + "`= :" + DbConst.STUDENT_MAILING_STREET_COL + ", `" +
                 DbConst.STUDENT_MAILING_CITY_COL + "`= :" + DbConst.STUDENT_MAILING_CITY_COL + ", `" +
                 DbConst.STUDENT_MAILING_STATE_COL + "`= :" + DbConst.STUDENT_MAILING_STATE_COL + ", `" +
                 DbConst.STUDENT_MAILING_POSTAL_COL + "`= :" + DbConst.STUDENT_MAILING_POSTAL_COL + ", `" +
                 DbConst.STUDENT_HOME_STREET_COL + "`= :" + DbConst.STUDENT_HOME_STREET_COL + ", `" +
                 DbConst.STUDENT_HOME_CITY_COL + "`= :" + DbConst.STUDENT_HOME_CITY_COL + ", `" +
                 DbConst.STUDENT_HOME_STATE_COL + "`= :" + DbConst.STUDENT_HOME_STATE_COL + ", `" +
                 DbConst.STUDENT_HOME_POSTAL_COL + "`= :" + DbConst.STUDENT_HOME_POSTAL_COL + ", `" +
                 DbConst.STUDENT_GENDER_COL + "`= :" + DbConst.STUDENT_GENDER_COL + ", `" +
                 DbConst.STUDENT_BIRTH_DATE_COL + "`= :" + DbConst.STUDENT_BIRTH_DATE_COL + ", `" +
                 DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL + "`= :" + DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL + ", `" +
                 DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL + "`= :" + DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL + ", `" +
                 DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL + "`= :" + DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL + ", `" +
                 DbConst.STUDENT_RACE_COL + "`= :" + DbConst.STUDENT_RACE_COL + ", `" +
                 DbConst.SCHOOL_FK_COL + "`= :" + DbConst.SCHOOL_FK_COL + ", `" +
                 DbConst.STUDENT_ETHNICITY_COL + "`= :" + DbConst.STUDENT_ETHNICITY_COL + " " +
            "WHERE `" + DbConst.STUDENT_ID_COL + "`= :" + DbConst.STUDENT_ID_COL + "";

    private static String SELECT_ALL_STUDENTS_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "`";
    
    private String SELECT_STUDENTS_IN_SECTION_SQL = SELECT_ALL_STUDENTS_SQL +
            " INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`" + 
            " ON `" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`.`" + DbConst.STUD_FK_COL + "` = `" +
            DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` " +
            "WHERE `" + DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL;

    @Override
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        List<Student> students = jdbcTemplate.query(
                SELECT_STUDENTS_IN_SECTION_SQL, 
                params,
                new StudentMapper());
        return students;
    }
    
    @Override
    public Long createStudent(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUDENT_NAME_COL, student.getName());
        params.put(DbConst.STUDENT_SOURCE_SYSTEM_ID, student.getSourceSystemId());
        Address mailingAddress = student.getMailingAddress();
        if(null == mailingAddress) {
            mailingAddress = new Address();
        }
        params.put(DbConst.STUDENT_MAILING_STREET_COL, mailingAddress.getStreet());
        params.put(DbConst.STUDENT_MAILING_CITY_COL, mailingAddress.getCity());
        params.put(DbConst.STUDENT_MAILING_STATE_COL, mailingAddress.getState());
        params.put(DbConst.STUDENT_MAILING_POSTAL_COL, mailingAddress.getPostalCode());
        Address homeAddress = student.getMailingAddress();
        if(null == homeAddress) {
            homeAddress = new Address();
        }
        params.put(DbConst.STUDENT_HOME_STREET_COL, homeAddress.getStreet());
        params.put(DbConst.STUDENT_HOME_CITY_COL, homeAddress.getCity());
        params.put(DbConst.STUDENT_HOME_STATE_COL, homeAddress.getState());
        params.put(DbConst.STUDENT_HOME_POSTAL_COL, homeAddress.getPostalCode());

        if (null != student.getGender()) {
            params.put(DbConst.STUDENT_GENDER_COL, student.getGender().name());
        }
        params.put(DbConst.STUDENT_BIRTH_DATE_COL, student.getBirthDate());
        params.put(DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL, student.getDistrictEntryDate());
        params.put(DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL, student.getProjectedGraduationYear());
        params.put(DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL, student.getSocialSecurityNumber());
        params.put(DbConst.STUDENT_RACE_COL, student.getFederalRace());
        params.put(DbConst.STUDENT_ETHNICITY_COL, student.getFederalEthnicity());
        params.put(DbConst.SCHOOL_FK_COL, student.getCurrentSchoolId());
        jdbcTemplate.update(INSERT_STUDENT_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }
    @Override
    public Long replaceStudent(long studentId, Student student) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUDENT_NAME_COL, student.getName());
        params.put(DbConst.STUDENT_ID_COL, new Long(studentId));
        params.put(DbConst.STUDENT_SOURCE_SYSTEM_ID, student.getSourceSystemId());
        Address mailingAddress = student.getMailingAddress();
        if(null == mailingAddress) {
            mailingAddress = new Address();
        }
        params.put(DbConst.STUDENT_MAILING_STREET_COL, mailingAddress.getStreet());
        params.put(DbConst.STUDENT_MAILING_CITY_COL, mailingAddress.getCity());
        params.put(DbConst.STUDENT_MAILING_STATE_COL, mailingAddress.getState());
        params.put(DbConst.STUDENT_MAILING_POSTAL_COL, mailingAddress.getPostalCode());
        Address homeAddress = student.getMailingAddress();
        if(null == homeAddress) {
            homeAddress = new Address();
        }
        params.put(DbConst.STUDENT_HOME_STREET_COL, homeAddress.getStreet());
        params.put(DbConst.STUDENT_HOME_CITY_COL, homeAddress.getCity());
        params.put(DbConst.STUDENT_HOME_STATE_COL, homeAddress.getState());
        params.put(DbConst.STUDENT_HOME_POSTAL_COL, homeAddress.getPostalCode());
        params.put(DbConst.STUDENT_GENDER_COL, student.getGender());
        params.put(DbConst.STUDENT_BIRTH_DATE_COL, DbConst.resolveTimestamp(student.getBirthDate()));
        params.put(DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL, DbConst.resolveTimestamp(student.getDistrictEntryDate()));
        params.put(DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL, student.getProjectedGraduationYear());
        params.put(DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL, student.getSocialSecurityNumber());
        params.put(DbConst.STUDENT_RACE_COL, student.getFederalRace());
        params.put(DbConst.STUDENT_ETHNICITY_COL, student.getFederalEthnicity());
        params.put(DbConst.SCHOOL_FK_COL, student.getCurrentSchoolId());
        jdbcTemplate.update(UPDATE_STUDENT_SQL, new MapSqlParameterSource(params));
        return studentId;
    }

    @Override
    public RowMapper<Student> getMapper() {
        return new StudentMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.STUDENT_TABLE;
    }
}
