package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.query.expressions.DateDimension;
import com.scholarscore.models.query.expressions.Expression;

/**
 * Represents a query for data, the execution of which results in a table of
 * data, which may be used in various types of reports. A query is made of
 * aggregate measures, dimensions, and filter criteria. The warehouse module is
 * capable of converting a query defined into this manner into an actual
 * SQL query and returning table result sets.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Query extends ApiModel implements Serializable, IApiModel<Query> {
    // In SQL terms, this collection defines the columns that are being SUM'd, AVG'd, or otherwise aggregated
    LinkedHashSet<AggregateMeasure> aggregateMeasures;
    // In SQL terms, the Dimension represents those columns in the GROUP BY clause.
    // In our model, a dimension may be a complex object leading to multiple columns in a returned
    // SQL result set, but a dimension can generally be thought of correlating to a single table
    Dimension dimension;
    // A date dimension can be used in conjunction with other dimensions (e.g. GROUP BY teacher, week)
    // Or can be used in a stand alone fashion for example to group all detensions by week.
    DateDimension dateDimension;
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
        if (null == this.dimension) {
            this.dimension = query.dimension;
        }
        if (null == this.filter) {
            this.filter = query.filter;
        }
        if (null == this.dateDimension) {
            this.dateDimension = query.dateDimension;
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

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    public DateDimension getDateDimension() {
        return dateDimension;
    }

    public void setDateDimension(DateDimension dateDimension) {
        this.dateDimension = dateDimension;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Query other = (Query) obj;
        return Objects.equals(this.aggregateMeasures, other.aggregateMeasures)
                && Objects.equals(this.dimension, other.dimension)
                && Objects.equals(this.filter, other.filter)
                && Objects.equals(this.dateDimension, other.dateDimension);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(aggregateMeasures, dimension, filter, dateDimension);
    }

}
