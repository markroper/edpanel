package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SchoolMapper;
import com.scholarscore.models.School;

public class SchoolJdbc extends EnhancedBaseJdbc<School> implements SchoolPersistence {
    
    private static String INSERT_SCHOOL_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "` " +
            "(" + DbConst.SCHOOL_NAME_COL + ")" +
            " VALUES (:name)";   

    private static String UPDATE_SCHOOL_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.SCHOOL_TABLE + "` " + 
            "SET `" + DbConst.SCHOOL_NAME_COL + "`= :name " + 
            "WHERE `" + DbConst.SCHOOL_ID_COL + "`= :id";

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#getSchool(long)
     */
    @Override
    public School selectSchool(long schoolId) {
        return super.select(schoolId);
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
    
    @Override
    public RowMapper<School> getMapper() {
        return new SchoolMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.SCHOOL_TABLE;
    }
}
