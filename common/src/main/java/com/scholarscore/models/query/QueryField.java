package com.scholarscore.models.query;

import com.scholarscore.models.query.bucket.AggregationBucket;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 2/9/16.
 */
public abstract class QueryField implements Serializable {
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
    // If a dimension field has buckets the bucket output replaces the raw value of the dimension
    // field but does not spawn a new field because there is no need to aggregate on it.
    //
    //Buckets can be null. If they're not, each bucket in the list must have a unique label
    //and each bucket in the list must have an 'end' that is equal to or greater than the 'start' value.
    //Either the end value or the start value can be null to match 'everything less than' or 'everything greater
    //than or equal to' respectively. If 'start' and 'end' are both null, the bucket is assumed to contain the
    //default value and for this reason, should be last in the list.
    List<AggregationBucket> buckets;

    public List<AggregationBucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<AggregationBucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buckets);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final QueryField other = (QueryField) obj;
        return Objects.equals(this.buckets, other.buckets);
    }
}
