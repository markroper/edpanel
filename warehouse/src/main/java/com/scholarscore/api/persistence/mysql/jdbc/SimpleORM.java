package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.DbConst;
import org.springframework.jdbc.core.RowMapper;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by mattg on 7/20/15.
 */
public abstract class SimpleORM<T> extends EnhancedBaseJdbc<T> {

    public class Field {
        private final String name;
        private final JDBCType type;

        public Field(String name, JDBCType type) {
            this.name = name;
            this.type = type;
        }
    }

    public String getDatabaseName() {
        return DbConst.DATABASE;
    }

    public abstract RowMapper<T> getMapper();
    public abstract LinkedHashSet<Field> getFieldNames();

    protected Collection<T> selectAll() {
        jdbcTemplate.query(selectAllSql,
                params,
                getMapper());
    }

    protected String generateInsert() {
        String database = getDatabaseName();
        String tableName = getTableName();
        LinkedHashSet<Field> fields = getFieldNames();

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO `");
        sql.append(database);
        sql.append("`.`");
        sql.append(tableName);
        sql.append("` ");
        sql.append("(");
        StringBuffer columns = new StringBuffer();
        fields.forEach(field -> {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(":").append(field.name);
        });
        sql.append(columns);
        sql.append(" VALUES (");
        sql.append(columns);
        sql.append(")");

        return sql.toString();
    }

    protected String generateUpdate() {
        StringBuffer sql = new StringBuffer("UPDATE `");
        sql.append(getDatabaseName());
        sql.append("`.`");
        sql.append(getTableName());
        sql.append("` ");
        sql.append("SET `");
        StringBuffer columns = new StringBuffer();
        getFieldNames().forEach(field -> {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(":")
                    .append(field.name)
                    .append(" = :")
                    .append(field.name)
                    .append(" ");
        });
        sql.append(columns);
        sql.append("WHERE `").append(":").append(getIdColName()).append(" = :").append(getIdColName());
        return sql.toString();
    }
}
