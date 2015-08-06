package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.HashMap;
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
            "(" + DbConst.SCHOOL_NAME_COL + ", " + DbConst.SCHOOL_SOURCE_SYSTEM_ID_COL + ")" +
            " VALUES (:name, :sourceSystemId)";

    private static String UPDATE_SCHOOL_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.SCHOOL_TABLE + "` " + 
            "SET `" + DbConst.SCHOOL_NAME_COL + "`= :name, " +
            "`" + DbConst.SCHOOL_SOURCE_SYSTEM_ID_COL + "` = :sourceSystemId " +
            "WHERE `" + DbConst.SCHOOL_ID_COL + "`= :id";

    private static String SCHOOL_COLUMNS = "`" + DbConst.SCHOOL_ID_COL
        + "`, `"
        + DbConst.SCHOOL_NAME_COL
        + ", "
        + DbConst.SCHOOL_SOURCE_SYSTEM_ID_COL;

    private static final String SELECT_SCHOOL_SQL = "SELECT "
            + SCHOOL_COLUMNS
            + " FROM `"
            + DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "`"
            + " WHERE " + DbConst.SCHOOL_ID_COL + " = :" + DbConst.SCHOOL_ID_COL;

    private static final String SELECT_ALL_SCHOOLS_SQL = "SELECT "
            + SCHOOL_COLUMNS
            + " FROM `"
                + DbConst.DATABASE +"`.`" + DbConst.SCHOOL_TABLE + "`";

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#getSchool(long)
     */
    @Override
    public School selectSchool(Long schoolId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.SCHOOL_ID_COL, schoolId);
        return super.select(params, SELECT_SCHOOL_SQL);
    }

    /* (non-Javadoc)
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolPersistence#createSchool(com.scholarscore.models.School)
     */
    @Override
    public Long createSchool(School school) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put("name", school.getName());
        params.put("sourceSystemId", school.getSourceSystemId());
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
        params.put("sourceSystemId", school.getSourceSystemId());
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
