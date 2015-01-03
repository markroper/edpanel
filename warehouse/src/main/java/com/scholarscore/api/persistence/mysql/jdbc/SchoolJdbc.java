package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.SchoolManager;
import com.scholarscore.api.persistence.mysql.DBConsts;
import com.scholarscore.api.persistence.mysql.mapper.SchoolMapper;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.School;

public class SchoolJdbc implements SchoolManager {
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private static String INSERT_SCHOOL_SQL = "INSERT INTO `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "` " +
            "(" + DBConsts.SCHOOL_NAME_COL + ")" +
            " VALUES (:name)";   
    private static String UPDATE_SCHOOL_SQL = 
            "UPDATE `" + DBConsts.DATABASE + "`.`" + DBConsts.SCHOOL_TABLE + "` " + 
            "SET `" + DBConsts.SCHOOL_NAME_COL + "`= :name " + 
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= :id";
    private static String DELETE_SCHOOL_SQL = "DELETE FROM `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "` " +
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= :id";
    private static String SELECT_ALL_SCHOOLS_SQL = "SELECT * FROM `"+ 
            DBConsts.DATABASE +"`.`" + DBConsts.SCHOOL_TABLE + "`";
    private static String SELECT_SCHOOL_SQL = SELECT_ALL_SCHOOLS_SQL + 
            "WHERE `" + DBConsts.SCHOOL_ID_COL + "`= :id";
    
    public void setDataSource(DataSource dataSource) {
       this.dataSource = dataSource;
       this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    public Collection<School> getAllSchools() {
        Collection<School> students = jdbcTemplate.query(SELECT_ALL_SCHOOLS_SQL, 
                new SchoolMapper());
        return students;
    }

    @Override
    public StatusCode schoolExists(long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("id", new Long(schoolId));     
        List<School> school = jdbcTemplate.query(
                SELECT_SCHOOL_SQL, 
                params, 
                new SchoolMapper());
        if(null == school || school.isEmpty()) {
            return new StatusCode(StatusCodes.MODEL_NOT_FOUND, new Object[] { "school", schoolId }); 
        }
        return StatusCodes.OK;
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<School>(code);
        }
        Map<String, Object> params = new HashMap<>();     
        params.put("id", new Long(schoolId));
        List<School> school = jdbcTemplate.query(
                SELECT_SCHOOL_SQL, 
                params, 
                new SchoolMapper());
        return new ServiceResponse<School>(school.get(0));
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put("name", school.getName());
        jdbcTemplate.update(INSERT_SCHOOL_SQL, new MapSqlParameterSource(params), keyHolder);
        return new ServiceResponse<Long>(keyHolder.getKey().longValue());
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code);
        }
        Map<String, Object> params = new HashMap<>();     
        params.put("name", school.getName());
        params.put("id", new Long(schoolId));
        jdbcTemplate.update(UPDATE_SCHOOL_SQL, new MapSqlParameterSource(params));
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> updateSchool(long schoolId,
            School partialSchool) {
        ServiceResponse<School> sr = getSchool(schoolId);
        if(null == sr.getValue()) {
            return new ServiceResponse<Long>(sr.getCode());
        }
        School originalSchool = sr.getValue();
        partialSchool.mergePropertiesIfNull(originalSchool);
        Map<String, Object> params = new HashMap<>();
        params.put("name", partialSchool.getName());
        params.put("id", new Long(schoolId));
        jdbcTemplate.update(UPDATE_SCHOOL_SQL, new MapSqlParameterSource(params));
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.equals(StatusCodes.OK)) {
            return new ServiceResponse<Long>(code);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", new Long(schoolId));
        jdbcTemplate.update(DELETE_SCHOOL_SQL, new MapSqlParameterSource(params));
        return new ServiceResponse<Long>(schoolId);
    }
}
