package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Administrator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static java.sql.JDBCType.VARCHAR;

public class AdministratorJdbc extends SimpleORM<Administrator>
        implements AdministratorPersistence {

    private static final LinkedHashSet<Field> fields =
            new LinkedHashSet<>(Arrays.asList(
                    new Field(DbConst.ADMINISTRATOR_NAME_COL, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_SOURCE_SYSTEM_ID_COL, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_USERNAME_COL, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_HOME_STREET, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_HOME_CITY, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_HOME_STATE, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_HOME_POSTAL_CODE, VARCHAR),
                    new Field(DbConst.ADMINISTRATOR_HOME_PHONE, VARCHAR)
            )
            );

    @Override
    public Long createAdministrator(Administrator administrator) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.ADMINISTRATOR_NAME_COL, administrator.getName());
        params.put(DbConst.ADMINISTRATOR_SOURCE_SYSTEM_ID_COL, administrator.getSourceSystemId());
        if (null != administrator.getLogin()) {
            params.put(DbConst.ADMINISTRATOR_USERNAME_COL, administrator.getLogin().getUsername());
        }
        if (null != administrator.getHomeAddress()) {
            params.put(DbConst.ADMINISTRATOR_HOME_STREET, administrator.getHomeAddress().getStreet());
            params.put(DbConst.ADMINISTRATOR_HOME_CITY, administrator.getHomeAddress().getCity());
            params.put(DbConst.ADMINISTRATOR_HOME_STATE, administrator.getHomeAddress().getState());
            params.put(DbConst.ADMINISTRATOR_HOME_POSTAL_CODE, administrator.getHomeAddress().getPostalCode());
        }
        params.put(DbConst.ADMINISTRATOR_HOME_PHONE, administrator.getHomePhone());
        jdbcTemplate.update(generateInsert(), new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void replaceAdministrator(long administratorId, Administrator administrator) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.ADMINISTRATOR_NAME_COL, administrator.getName());
        params.put(DbConst.ADMINISTRATOR_SOURCE_SYSTEM_ID_COL, administrator.getSourceSystemId());
        if (null != administrator.getLogin()) {
            params.put(DbConst.ADMINISTRATOR_USERNAME_COL, administrator.getLogin().getUsername());
        }
        params.put(DbConst.ADMINISTRATOR_ID_COL, new Long(administratorId));
        if (null != administrator.getHomeAddress()) {
            params.put(DbConst.ADMINISTRATOR_HOME_STREET, administrator.getHomeAddress().getStreet());
            params.put(DbConst.ADMINISTRATOR_HOME_CITY, administrator.getHomeAddress().getCity());
            params.put(DbConst.ADMINISTRATOR_HOME_STATE, administrator.getHomeAddress().getState());
            params.put(DbConst.ADMINISTRATOR_HOME_POSTAL_CODE, administrator.getHomeAddress().getPostalCode());
        }
        params.put(DbConst.ADMINISTRATOR_HOME_PHONE, administrator.getHomePhone());
        jdbcTemplate.update(generateUpdate(), new MapSqlParameterSource(params));
    }

    @Override
    public RowMapper<Administrator> getMapper() {
        return new RowMapper<Administrator>() {
            @Override
            public Administrator mapRow(ResultSet rs, int rowNum) throws SQLException {
                Administrator administrator = new Administrator();
                administrator.setId(rs.getLong(DbConst.ADMINISTRATOR_ID_COL));
                administrator.setName(rs.getString(DbConst.ADMINISTRATOR_NAME_COL));
                return administrator;
            }
        };
    }

    @Override
    public LinkedHashSet<Field> getFields() {
        return fields;
    }

    @Override
    public String getTableName() {
        return DbConst.ADMINISTRATOR_TABLE;
    }
}
