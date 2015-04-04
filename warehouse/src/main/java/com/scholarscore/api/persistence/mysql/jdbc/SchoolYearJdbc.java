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
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SchoolYearMapper;
import com.scholarscore.models.SchoolYear;

public class SchoolYearJdbc extends EnhancedBaseJdbc<SchoolYear> implements EntityPersistence<SchoolYear> {
    private static String INSERT_SCHOOL_YEAR_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_YEAR_TABLE + "` " +
            "(" + DbConst.SCHOOL_YEAR_NAME_COL + ", " + DbConst.SCHOOL_FK_COL + ", " + 
            DbConst.SCHOOL_YEAR_START_DATE_COL + ", " + DbConst.SCHOOL_YEAR_END_DATE_COL + ")" +
            " VALUES (:" + DbConst.SCHOOL_YEAR_NAME_COL + ", :" + DbConst.SCHOOL_FK_COL + 
            ", :" + DbConst.SCHOOL_YEAR_START_DATE_COL + ", :" + DbConst.SCHOOL_YEAR_END_DATE_COL + ")";
    
    private static String UPDATE_SCHOOL_YEAR_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.SCHOOL_YEAR_TABLE + "` " + 
            "SET `" + DbConst.SCHOOL_YEAR_NAME_COL + "`= :" + DbConst.SCHOOL_YEAR_NAME_COL + ", `" +
            DbConst.SCHOOL_FK_COL + "`= :" + DbConst.SCHOOL_FK_COL + ", `" +
            DbConst.SCHOOL_YEAR_START_DATE_COL + "`= :" + DbConst.SCHOOL_YEAR_START_DATE_COL + ", `" +
            DbConst.SCHOOL_YEAR_END_DATE_COL + "`= :" + DbConst.SCHOOL_YEAR_END_DATE_COL + " " +
            "WHERE `" + DbConst.SCHOOL_YEAR_ID_COL + "`= :" + DbConst.SCHOOL_YEAR_ID_COL + "";
    
    private static String SELECT_ALL_SCHOOL_YEARS_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.SCHOOL_YEAR_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_FK_COL + "` = :" + DbConst.SCHOOL_FK_COL;
    
    private static String SELECT_SCHOOL_YEAR_SQL = SELECT_ALL_SCHOOL_YEARS_SQL + 
            " AND `" + DbConst.SCHOOL_YEAR_ID_COL + "`= :" + DbConst.SCHOOL_YEAR_ID_COL;
    
    /**
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolYearPersistence#selectAllSchoolYears(long)
     */
    @Override
    public Collection<SchoolYear> selectAll(
            long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        return super.selectAll(params);
    }

    /**
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolYearPersistence#selectSchoolYear(long, long)
     */
    @Override
    public SchoolYear select(long schoolId, long schoolYearId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.SCHOOL_YEAR_ID_COL, new Long(schoolYearId));
        List<SchoolYear> schoolYears = jdbcTemplate.query(
                SELECT_SCHOOL_YEAR_SQL, 
                params, 
                new SchoolYearMapper());
        SchoolYear year = null;
        if(null != schoolYears && !schoolYears.isEmpty()) {
            year = schoolYears.get(0);
        }
        return year;
    }

    /**
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolYearPersistence#insertSchoolYear(long, com.scholarscore.models.SchoolYear)
     */
    @Override
    public Long insert(long schoolId, SchoolYear schoolYear) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_YEAR_NAME_COL, schoolYear.getName());
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.SCHOOL_YEAR_START_DATE_COL, DbConst.resolveTimestamp(schoolYear.getStartDate()));
        params.put(DbConst.SCHOOL_YEAR_END_DATE_COL, DbConst.resolveTimestamp(schoolYear.getEndDate()));
        params.put(DbConst.SCHOOL_YEAR_NAME_COL, schoolYear.getName());
        jdbcTemplate.update(
                INSERT_SCHOOL_YEAR_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    /**
     * @see com.scholarscore.api.persistence.mysql.jdbc.SchoolYearPersistence#updateSchoolYear(long, long, com.scholarscore.models.SchoolYear)
     */
    @Override
    public Long update(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_YEAR_NAME_COL, schoolYear.getName());
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.SCHOOL_YEAR_START_DATE_COL, DbConst.resolveTimestamp(schoolYear.getStartDate()));
        params.put(DbConst.SCHOOL_YEAR_END_DATE_COL, DbConst.resolveTimestamp(schoolYear.getEndDate()));
        params.put(DbConst.SCHOOL_YEAR_ID_COL, schoolYearId);
        jdbcTemplate.update(
                UPDATE_SCHOOL_YEAR_SQL, 
                new MapSqlParameterSource(params));
        return schoolId;
    }

    @Override
    protected String getSelectAllSQL() {
        return SELECT_ALL_SCHOOL_YEARS_SQL;
    }

    @Override
    public RowMapper<SchoolYear> getMapper() {
        return new SchoolYearMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.SCHOOL_YEAR_TABLE;
    }

}
