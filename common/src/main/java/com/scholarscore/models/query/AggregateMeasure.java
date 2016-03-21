package com.scholarscore.models.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.bucket.AggregationBucket;

import java.io.Serializable;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregateMeasure extends QueryField implements Serializable {
    Measure measure;
    AggregateFunction aggregation;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final AggregateMeasure other = (AggregateMeasure) obj;
        return Objects.equals(this.measure, other.measure)
                && Objects.equals(this.aggregation, other.aggregation);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(measure, aggregation);
    }
}
