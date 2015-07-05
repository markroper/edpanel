package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.Objects;

/**
 * Encapsulates a measure value and the aggregate function to use when querying
 * for that measure in an aggregate query.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class AggregateMeasure implements Serializable {
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
        final AggregateMeasure other = (AggregateMeasure) obj;
        return Objects.equals(this.measure, other.measure)
                && Objects.equals(this.aggregation, other.aggregation);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(measure, aggregation);
    }
}
