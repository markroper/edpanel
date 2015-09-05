package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResults implements Serializable {
    List<Record> records;
    List<Object> fieldInfo;
    Query query;
    
    public QueryResults() {
        
    }
    
    public QueryResults(List<Record> records) {
        this.records = records;
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
        return 31 * super.hashCode() + Objects.hash(records, fieldInfo, query);
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final QueryResults other = (QueryResults) obj;
        return Objects.equals(this.records, other.records) 
                && Objects.equals(this.fieldInfo, other.fieldInfo) 
                && Objects.equals(this.query, other.query);
    }
    
}
