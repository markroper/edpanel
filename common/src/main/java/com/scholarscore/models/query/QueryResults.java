package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class QueryResults implements Serializable {
    List<Record> records;
    List<Object> fieldInfo;
    Query query;
    
    public QueryResults() {
        
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<Object> getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(List<Object> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fieldInfo == null) ? 0 : fieldInfo.hashCode());
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        result = prime * result + ((records == null) ? 0 : records.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryResults other = (QueryResults) obj;
        if (fieldInfo == null) {
            if (other.fieldInfo != null)
                return false;
        } else if (!fieldInfo.equals(other.fieldInfo))
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        if (records == null) {
            if (other.records != null)
                return false;
        } else if (!records.equals(other.records))
            return false;
        return true;
    }
    
}
