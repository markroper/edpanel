package com.scholarscore.api.querygenerator;

import java.util.Map;
import java.util.Objects;

public class SqlWithParameters {
    protected String sql;
    protected Map<String, Object> params;
    
    public SqlWithParameters(String sql, Map<String, Object> params) {
        this.sql = sql;
        this.params = params;
    }
    
    public String getSql() {
        return sql;
    }


    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getParams() {
        return params;
    }


    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SqlWithParameters other = (SqlWithParameters) obj;
        return Objects.equals(this.sql, other.sql)
                && Objects.equals(this.params, other.params);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(sql, params);
    }
}
