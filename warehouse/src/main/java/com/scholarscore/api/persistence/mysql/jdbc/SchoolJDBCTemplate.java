package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.SchoolManager;
import com.scholarscore.api.persistence.mysql.DBConsts;
import com.scholarscore.api.persistence.mysql.mapper.SchoolMapper;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.School;

public class SchoolJDBCTemplate implements SchoolManager {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    
    private String INSERT_SCHOOL_SQL = "INSERT INTO `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "` " +
            "(" + DBConsts.SCHOOL_NAME_COL + ")" +
            " VALUES (?)";
    
    private String UPDATE_SCHOOL_SQL = 
            "UPDATE `" + DBConsts.DATABASE + "`.`" + DBConsts.SCHOOL_TABLE + "` " + 
            "SET `" + DBConsts.SCHOOL_NAME_COL + "`= ? " + 
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= ?";
    
    private String DELETE_SCHOOL_SQL = "DELETE FROM `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "` " +
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= ?";
    
    private String SELECT_ALL_SCHOOLS_SQL = "SELECT * FROM `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "`";
    
    private String SELECT_SCHOOL_SQL = SELECT_ALL_SCHOOLS_SQL + 
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= ?";
    
    public void setDataSource(DataSource dataSource) {
       this.dataSource = dataSource;
       this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }
    
    @Override
    public Collection<School> getAllSchools() {
        Collection<School> students = jdbcTemplateObject.query(SELECT_ALL_SCHOOLS_SQL, 
                new SchoolMapper());
        return students;
    }

    @Override
    public StatusCode schoolExists(long schoolId) {
        School school = jdbcTemplateObject.queryForObject(
                SELECT_SCHOOL_SQL, 
                new Object[]{ schoolId }, 
                new SchoolMapper());
        if(null == school) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { "school", schoolId }); 
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        School school = jdbcTemplateObject.queryForObject(
                SELECT_SCHOOL_SQL, 
                new Object[]{ schoolId }, 
                new SchoolMapper());
        return new ServiceResponse<School>(school);
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplateObject.update(INSERT_SCHOOL_SQL, new Object[]{ school.getName() }, keyHolder);
        return new ServiceResponse<Long>(keyHolder.getKey().longValue());
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        //TODO: resolve how we want to handle the replace vs. update at the data layer...
        return updateSchool(schoolId, school);
    }

    @Override
    public ServiceResponse<Long> updateSchool(long schoolId,
            School partialSchool) {
        jdbcTemplateObject.update(UPDATE_SCHOOL_SQL, 
                new Object[] { partialSchool.getName(), new Long(schoolId) });
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        jdbcTemplateObject.update(DELETE_SCHOOL_SQL, schoolId);
        return new ServiceResponse<Long>(schoolId);
    }

}
