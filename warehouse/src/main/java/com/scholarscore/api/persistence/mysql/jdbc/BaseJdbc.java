package com.scholarscore.api.persistence.mysql.jdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseJdbc {
    protected DataSource dataSource;
    protected NamedParameterJdbcTemplate jdbcTemplate;
    //private SchoolJdbc schoolPersistence;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    
}
