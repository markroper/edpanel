package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SchoolMapper;
import com.scholarscore.models.School;

public class SchoolJdbc extends BaseJdbc implements SchoolPersistence {
    private static String INSERT_SCHOOL_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "` " +
            "(" + DbConst.SCHOOL_NAME_COL + ")" +
            " VALUES (:name)";   
    private static String UPDATE_SCHOOL_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.SCHOOL_TABLE + "` " + 
            "SET `" + DbConst.SCHOOL_NAME_COL + "`= :name " + 
            "WHERE `" + DbConst.SCHOOL_ID_COL + "`= :id";
    private static String DELETE_SCHOOL_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_ID_COL + "`= :id";
    private static String SELECT_ALL_SCHOOLS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "`";
    private static String SELECT_SCHOOL_SQL = SELECT_ALL_SCHOOLS_SQL + 
            "WHERE `" + DbConst.SCHOOL_ID_COL + "`= :id";
    
    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#getAllSchools()
     */
    @Override
    public Collection<School> selectAllSchools() {
        Collection<School> students = jdbcTemplate.query(SELECT_ALL_SCHOOLS_SQL, 
                new SchoolMapper());
        return students;
    }

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#getSchool(long)
     */
    @Override
    public School selectSchool(long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("id", new Long(schoolId));
        List<School> schools = jdbcTemplate.query(
                SELECT_SCHOOL_SQL, 
                params, 
                new SchoolMapper());
        School school = null;
        if(null != schools && !schools.isEmpty()) {
            school = schools.get(0);
        }
        return school;
    }

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#createSchool(com.scholarscore.models.School)
     */
    @Override
    public Long createSchool(School school) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put("name", school.getName());
        jdbcTemplate.update(INSERT_SCHOOL_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#replaceSchool(long, com.scholarscore.models.School)
     */
    @Override
    public Long replaceSchool(long schoolId, School school) {
        Map<String, Object> params = new HashMap<>();     
        params.put("name", school.getName());
        params.put("id", new Long(schoolId));
        jdbcTemplate.update(UPDATE_SCHOOL_SQL, new MapSqlParameterSource(params));
        return schoolId;
    }

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#deleteSchool(long)
     */
    @Override
    public Long deleteSchool(long schoolId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", new Long(schoolId));
        jdbcTemplate.update(DELETE_SCHOOL_SQL, new MapSqlParameterSource(params));
        return schoolId;
    }
}
