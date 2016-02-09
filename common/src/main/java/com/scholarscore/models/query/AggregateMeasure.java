package com.scholarscore.models.query;

import com.scholarscore.models.query.bucket.AggregationBucket;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates a measure value and the aggregate function to use when querying
 * for that measure in an aggregate query.
 *
 * @see AggregationBucket
 * @see AggregateFunction
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class AggregateMeasure implements Serializable {
    Measure measure;
    AggregateFunction aggregation;
    //If an AggregateMeasure has buckets, aggregate values are bucketed according to the buckets defined
    //and the bucket psuedo-column gets included in the GROUP BY. The aggregate column will also be generated
    //and NOT included in the group by.  For example:
    //
    //    count(*),
    //    case
    //      WHEN gpa_score >= 0 AND gpa_score < 1 THEN '0-1'
    //      WHEN gpa_score >= 1 AND gpa_score < 2 THEN '1-2'
    //      WHEN gpa_score >= 2 AND gpa_score < 3 THEN '2-3'
    //      WHEN gpa_score >= 3 AND gpa_score < 4 THEN '3-4'
    //      WHEN gpa_score >= 4 THEN '4+'
    //      ELSE NULL
    //    END as 'gpa_bucket'
    //    FROM gpa
    //    GROUP BY gpa_bucket
    //
    //Buckets can be null. If they're not, each bucket in the list must have a unique label
    //and each bucket in the list must have an 'end' that is equal to or greater than the 'start' value.
    //Either the end value or the start value can be null to match 'everything less than' or 'everything greater
    //than or equal to' respectively. If 'start' and 'end' are both null, the bucket is assumed to contain the
    //default value and for this reason, should be last in the list.
    List<AggregationBucket> buckets;

    public AggregateMeasure() {

    }

    public AggregateMeasure(Measure measure, AggregateFunction aggregation) {
        this.measure = measure;
        this.aggregation = aggregation;
    }

    // Getters, setters, equals(), hashCode(), toString()
    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public AggregateFunction getAggregation() {
        return aggregation;
    }

    public void setAggregation(AggregateFunction aggregation) {
        this.aggregation = aggregation;
    }

    public List<AggregationBucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<AggregationBucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AggregateMeasure other = (AggregateMeasure) obj;
        return Objects.equals(this.measure, other.measure)
                && Objects.equals(this.aggregation, other.aggregation)
                && Objects.equals(this.buckets, other.buckets);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(measure, aggregation, buckets);
    }
}
