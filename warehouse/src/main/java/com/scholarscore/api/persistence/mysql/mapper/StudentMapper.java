package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.models.Student;

public class StudentMapper implements RowMapper<Student>{

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong(DbConst.STUDENT_ID_COL));
        student.setName(rs.getString(DbConst.STUDENT_NAME_COL));
        student.setSourceSystemId(rs.getLong(DbConst.STUDENT_SOURCE_SYSTEM_ID));
        if(rs.wasNull()) {
            student.setSourceSystemId(null);
        }
        Address mailingAddress = new Address();
        mailingAddress.setStreet(rs.getString(DbConst.STUDENT_MAILING_STREET_COL));
        mailingAddress.setState(rs.getString(DbConst.STUDENT_MAILING_STATE_COL));
        mailingAddress.setPostalCode(rs.getString(DbConst.STUDENT_MAILING_POSTAL_COL));
        mailingAddress.setCity(rs.getString(DbConst.STUDENT_MAILING_CITY_COL));
        if(null != mailingAddress.getState() || null != mailingAddress.getCity()
                || null != mailingAddress.getPostalCode() || null != mailingAddress.getStreet()) {
            student.setMailingAddress(mailingAddress);
        }
        Address homeAddress = new Address();
        homeAddress.setStreet(rs.getString(DbConst.STUDENT_HOME_STREET_COL));
        homeAddress.setCity(rs.getString(DbConst.STUDENT_HOME_CITY_COL));
        homeAddress.setState(rs.getString(DbConst.STUDENT_HOME_STATE_COL));
        homeAddress.setPostalCode(rs.getString(DbConst.STUDENT_HOME_POSTAL_COL));
        if(null != homeAddress.getState() || null != homeAddress.getCity()
                || null != homeAddress.getState() || null != homeAddress.getPostalCode()) {
            student.setHomeAddress(homeAddress);
        }
        try {
            student.setGender(Gender.valueOf(rs.getString(DbConst.STUDENT_GENDER_COL)));
        } catch (IllegalArgumentException | NullPointerException e) {
            //No op
        }
        student.setBirthDate(rs.getTimestamp(DbConst.STUDENT_BIRTH_DATE_COL));
        student.setDistrictEntryDate(rs.getTimestamp(DbConst.STUDENT_DISTRICT_ENTRY_DATE_COL));
        student.setProjectedGraduationYear(rs.getLong(DbConst.STUDENT_PROJECTED_GRADUATION_DATE_COL));
        if(rs.wasNull()) {
            student.setProjectedGraduationYear(null);
        }
        student.setSocialSecurityNumber(rs.getString(DbConst.STUDENT_SOCIAL_SECURTIY_NUMBER_COL));
        student.setFederalRace(rs.getString(DbConst.STUDENT_RACE_COL));
        student.setFederalEthnicity(rs.getString(DbConst.STUDENT_ETHNICITY_COL));
        return student;
    }

}