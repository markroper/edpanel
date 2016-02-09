package com.scholarscore.models.query.bucket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a 'bucket', a list of which can be applied to the output from an aggregate measure
 * within an EdPanel query. These buckets are converted into SQL query switch statements of the form:
 *
 * select
 *  count(*),
 *  case
 *    WHEN gpa_score >= 0 AND gpa_score < 1 THEN '0-1'
 *    WHEN gpa_score >= 1 AND gpa_score < 2 THEN '1-2'
 *    WHEN gpa_score >= 2 AND gpa_score < 3 THEN '2-3'
 *    WHEN gpa_score >= 3 AND gpa_score < 4 THEN '3-4'
 *    WHEN gpa_score >= 4 THEN '4+'
 *    ELSE NULL
 *    END as 'gpa_bucket'
 *  from gpa
 *  group by gpa_bucket
 *
 * Created by markroper on 2/9/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateBucket.class, name = DateBucket.BUCKET_TYPE),
        @JsonSubTypes.Type(value = NumericBucket.class, name = NumericBucket.BUCKET_TYPE)
})
public abstract class AggregationBucket<T extends Comparable> implements Serializable {
    //Start value for the bucket inclusive
    private T start;
    //End value for the bucket, exclusive
    private T end;
    @Max(32)
    private String label;

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AggregationBucket other = (AggregationBucket) obj;
        return Objects.equals(this.start, other.start)
                && Objects.equals(this.end, other.end)
                && Objects.equals(this.label, other.label);
    }
}
