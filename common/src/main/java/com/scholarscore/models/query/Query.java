package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;

import com.scholarscore.models.ApiModel;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.query.expressions.Expression;

/**
 * Represents a query for data, the execution of which results in a table of
 * data, which may be used in various types of reports. A query is made of
 * aggregate measures, dimensions, and filter criteria. The warehouse module is
 * capable of converting a query defined into this manner into an actual
 * database query and returning table result sets.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class Query extends ApiModel implements Serializable, IApiModel<Query> {
    // In SQL terms, this collection defines the columns that are being SUM'd,
    // AVG'd, or otherwise aggregated
    LinkedHashSet<AggregateMeasure> aggregateMeasures;
    // In SQL terms, this collection defines the GROUP BY clause and the SELECT
    // column list
    // TODO: only certain combinations of group by columns really make sense,
    // how should we handle this?
    LinkedHashSet<Dimension> dimensions;
    // In SQL terms, this defines the WHERE clause of a query
    Expression filter;

    @Override
    public void mergePropertiesIfNull(Query mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == mergeFrom || !(mergeFrom instanceof Query)) {
            return;
        }
        Query query = (Query) mergeFrom;
        if (null == this.aggregateMeasures) {
            this.aggregateMeasures = query.aggregateMeasures;
        }
        if (null == this.dimensions) {
            this.dimensions = query.dimensions;
        }
        if (null == this.filter) {
            this.filter = query.filter;
        }

    }

    // Getters, setters, equals(), hashCode(), toString()
    public LinkedHashSet<AggregateMeasure> getAggregateMeasures() {
        return aggregateMeasures;
    }

    public void setAggregateMeasures(
            LinkedHashSet<AggregateMeasure> aggregateMeasures) {
        this.aggregateMeasures = aggregateMeasures;
    }

    public LinkedHashSet<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(LinkedHashSet<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Query other = (Query) obj;
        return Objects.equals(this.aggregateMeasures, other.aggregateMeasures)
                && Objects.equals(this.dimensions, other.dimensions)
                && Objects.equals(this.filter, other.filter);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(aggregateMeasures, dimensions, filter);
    }

}
