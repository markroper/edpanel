package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.query.expressions.Expression;

/**
 * Represents a query for data, the execution of which results in a table of
 * data, which may be used in various types of reports. A query is made of
 * aggregate measures, dimensions, and filter criteria. The warehouse module is
 * capable of converting a query defined into this manner into an actual
 * SQL query and returning table result sets.
 * 
 * {
 *   "aggregateMeasures": [
 *       {
 *           "measure": "DEMERITS",
 *           "aggregation": "SUM"
 *       },
 *       {
 *           "measure": "MERITS",
 *           "aggregation": "SUM"
 *       }
 *   ],
 *   "fields": [
 *       {
 *           "dimension": "STUDENT",
 *           "field": "Age"
 *       },
 *       {
 *           "dimension": "STUDENT",
 *           "field": "Ethnicity"
 *       },
 *       {
 *           "dimension": "SCHOOL",
 *           "field": "Address"
 *       }
 *   ],
 *   "filter": {
 *       "type": "EXPRESSION",
 *       "leftHandSide": {
 *           "type": "EXPRESSION",
 *           "leftHandSide": {
 *               "value": 61370280000000,
 *               "type": "DATE"
 *           },
 *           "operator": "GREATER_THAN_OR_EQUAL",
 *           "rightHandSide": {
 *               "value": "DATE",
 *               "type": "DIMENSION"
 *           }
 *       },
 *       "operator": "AND",
 *       "rightHandSide": {
 *           "type": "EXPRESSION",
 *           "leftHandSide": {
 *               "value": 1435944149361,
 *               "type": "DATE"
 *           },
 *           "operator": "LESS_THAN_OR_EQUAL",
 *           "rightHandSide": {
 *               "value": "DATE",
 *               "type": "DIMENSION"
 *           }
 *       }
 *   }
 * }
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
    LinkedHashSet<DimensionField> fields;
    // In SQL terms, this defines the WHERE clause of a query
    Expression filter;

    public Query() {
        
    }
    
    public Query(Query q) {
        aggregateMeasures = q.getAggregateMeasures();
        fields = q.getFields();
        filter = q.getFilter();
    }
    
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
        if (null == this.filter) {
            this.filter = query.filter;
        }
        if (null == this.fields) {
            this.fields = query.fields;
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

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    public LinkedHashSet<DimensionField> getFields() {
        return fields;
    }

    public void setFields(LinkedHashSet<DimensionField> fields) {
        this.fields = fields;
    }
    
    public void addField(DimensionField field) {
        if(null == this.fields) {
            this.fields = new LinkedHashSet<DimensionField>();
        }
        this.fields.add(field);
    }

    @JsonIgnore
    public boolean isValid() {
        //Are the measures in the AggregateMeasures compatible?
        Set<Measure> measuresInUse = new HashSet<Measure>();
        for(AggregateMeasure am: aggregateMeasures) {
            if(!measuresInUse.contains(am.getMeasure())) {
                for(Measure m : measuresInUse) {
                    if(!m.getCompatibleMeasures().contains(am.getMeasure())){
                        return false;
                    }
                }
                measuresInUse.add(am.getMeasure());
            }
        }
        //Are the dimensions in the DimensionFields compatible with one another and with the Measures?
        Set<Dimension> dimensionsInUse = new HashSet<Dimension>();
        for(DimensionField df : fields) {
            if(!dimensionsInUse.contains(df.getDimension())) {
                for(Dimension d : dimensionsInUse) {
                    //If neither the field dimension nor the other dimension are parents of one another
                    //The dimensions are incompatible for a single query.
                    if(!d.getParentDimensions().contains(df.getDimension()) &&
                            !df.getDimension().getParentDimensions().contains(d)) {
                        return false;
                    }
                }
                dimensionsInUse.add(df.getDimension());
                //Check that all measures in use are compatible with the new dimension
                for(Measure m: measuresInUse) {
                    if(!m.getCompatibleDimensions().contains(df.getDimension())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Query other = (Query) obj;
        return Objects.equals(this.aggregateMeasures, other.aggregateMeasures)
                && Objects.equals(this.filter, other.filter)
                && Objects.equals(this.fields, other.fields);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(aggregateMeasures, filter, fields);
    }

}
