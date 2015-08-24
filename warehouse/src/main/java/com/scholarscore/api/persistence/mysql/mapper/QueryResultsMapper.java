package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.models.query.Record;

/**
 * Capable of mapping the result set from an arbitrary database query into 
 * a Record instance, which is simply an array of Object instances.  Varchars
 * map to String instances, dates & timestamps to Java Date instances, and numerics
 * all map into Java Double instances.  Unmapped types are inserted into the Record 
 * as null values.
 * 
 * @author markroper
 *
 */
public class QueryResultsMapper implements RowMapper<Record>{

    @Override
    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Object> record = new ArrayList<Object>();
        ResultSetMetaData meta = rs.getMetaData();
        for(int i = 1; i <= meta.getColumnCount(); i++) {
            switch(meta.getColumnType(i)) {
            case Types.VARCHAR:
                record.add(rs.getString(i));
                break;
            case Types.DATE:
            case Types.TIMESTAMP:
                record.add(rs.getTimestamp(i));
                break;
            case Types.NUMERIC:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.FLOAT:
                record.add(new Double(rs.getDouble(i)));
                break;
            case Types.BOOLEAN:
                record.add(new Boolean(rs.getBoolean(i)));
                break;
            default:
                record.add(null);
                break;
            }
        }
        return new Record(record);
    }

}
