package com.scholarscore.models.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.util.EdPanelObjectMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
@Entity(name = HibernateConsts.REPORT_TABLE)
public class Query extends ApiModel implements Serializable, IApiModel<Query> {
    Long schoolId;
    // In SQL terms, this collection defines the columns that are being SUM'd, AVG'd, or otherwise aggregated
    List<AggregateMeasure> aggregateMeasures;
    // In SQL terms, the Dimension represents those columns in the GROUP BY clause.
    // In our model, a dimension may be a complex object leading to multiple columns in a returned
    // SQL result set, but a dimension can generally be thought of correlating to a single table
    List<DimensionField> fields;
    // In SQL terms, this defines the WHERE clause of a query
    Expression filter;
    //The having clause (where clause conditions that act upon the aggregate function values)
    Expression having;
    //Support for a parent query to aggregate the nested subquery
    List<SubqueryColumnRef> subqueryColumnsByPosition;
    //AND'd together only
    List<SubqueryExpression> subqueryFilter;
    // If the object(s) being queried will not be joined with the tables automatically pulled into the query,
    // an explicit join path can be specified here
    HashSet<Dimension> joinTables;
    
    public Query() {
        super();
    }
    
    public Query(Query q) {
        super(q);
        aggregateMeasures = q.getAggregateMeasures();
        fields = q.getFields();
        filter = q.getFilter();
        subqueryColumnsByPosition = q.getSubqueryColumnsByPosition();
        subqueryFilter = q.getSubqueryFilter();
        joinTables = q.getJoinTables();
        having = q.getHaving();
        schoolId = q.getSchoolId();
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
        if (null == this.subqueryColumnsByPosition) {
            this.subqueryColumnsByPosition = query.subqueryColumnsByPosition;
        }
        if (null == this.subqueryFilter) {
            this.subqueryFilter = query.subqueryFilter;
        }
        if (null == this.joinTables) {
            this.joinTables = query.joinTables;
        }
        if (null == this.having) {
            this.having = query.having;
        }
        if (null == this.schoolId) {
            this.schoolId = query.schoolId;
        }
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.REPORT_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolId() {
        return schoolId;
    }

    @JsonIgnore
    @Column(name = HibernateConsts.REPORT_TABLE, columnDefinition="text")
    public String getQuery() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @JsonIgnore
    public void setQuery(String qs) {
        Query q = null;
        try {
            q = EdPanelObjectMapper.MAPPER.readValue( qs, new TypeReference<Query>(){});
        } catch (IOException e) {
            q = new Query();
        }
        this.schoolId = q.schoolId;
        this.aggregateMeasures = q.aggregateMeasures;
        this.fields = q.fields;
        this.filter = q.filter;
        this.having = q.having;
        this.subqueryFilter = q.subqueryFilter;
        this.subqueryColumnsByPosition = q.subqueryColumnsByPosition;
        this.joinTables = q.joinTables;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    @Transient
    public String getName() {
        return super.getName();
    }

    // Getters, setters, equals(), hashCode(), toString()
    @Transient
    public List<SubqueryColumnRef> getSubqueryColumnsByPosition() {
        return subqueryColumnsByPosition;
    }

    public void setSubqueryColumnsByPosition(List<SubqueryColumnRef> subqueryColumnsByPosition) {
        this.subqueryColumnsByPosition = subqueryColumnsByPosition;
    }

    @Transient
    public Expression getHaving() {
        return having;
    }

    public void setHaving(Expression having) {
        this.having = having;
    }

    @Transient
    public List<SubqueryExpression> getSubqueryFilter() {
        return subqueryFilter;
    }

    public void setSubqueryFilter(List<SubqueryExpression> subqueryFilter) {
        this.subqueryFilter = subqueryFilter;
    }

    @Transient
    public List<AggregateMeasure> getAggregateMeasures() {
        return aggregateMeasures;
    }

    public void setAggregateMeasures(
            List<AggregateMeasure> aggregateMeasures) {
        this.aggregateMeasures = aggregateMeasures;
    }

    @Transient
    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    @Transient
    public List<DimensionField> getFields() {
        return fields;
    }

    public void setFields(List<DimensionField> fields) {
        this.fields = fields;
    }
    
    public void addField(DimensionField field) {
        if(null == this.fields) {
            this.fields = new ArrayList<DimensionField>();
        }
        this.fields.add(field);
    }

    @Transient
    public HashSet<Dimension> getJoinTables() {
        return joinTables;
    }

    public void setJoinTables(HashSet<Dimension> joinTables) {
        this.joinTables = joinTables;
    }

    // hint to this query that this table/dimension needs to be used when joining
    public void addJoinTable(Dimension dimension) { 
        if (null == this.joinTables) {
            this.joinTables = new HashSet<Dimension>();
        }
        this.joinTables.add(dimension);
    }

    @JsonIgnore
    @Transient
    public boolean isValid() {
        //Are the measures in the AggregateMeasures compatible?
        Set<Measure> measuresInUse = new HashSet<Measure>();
        if(null != aggregateMeasures) {
            for(AggregateMeasure am: aggregateMeasures) {
                if(!measuresInUse.contains(am.getMeasure())) {
                    for(Measure m : measuresInUse) {
                        if(!m.buildMeasure().getCompatibleMeasures().contains(am.getMeasure())){
                            return false;
                        }
                    }
                    measuresInUse.add(am.getMeasure());
                }
            }
        }
        //Are the dimensions in the DimensionFields compatible with one another and with the Measures?
        Set<Dimension> dimensionsInUse = new HashSet<Dimension>();
        if(null != fields) {
            for(DimensionField df : fields) {
                if(!dimensionsInUse.contains(df.getDimension())) {
                    for(Dimension d : dimensionsInUse) {
                        //If neither the field dimension nor the other dimension are parents of one another
                        //The dimensions are incompatible for a single query.
                        if(!d.buildDimension().getParentDimensions().contains(df.getDimension()) &&
                                !df.getDimension().buildDimension().getParentDimensions().contains(d)) {
                            return false;
                        }
                    }
                    dimensionsInUse.add(df.getDimension());
                    //Check that all measures in use are compatible with the new dimension
                    for(Measure m: measuresInUse) {
                        if(!m.buildMeasure().getCompatibleDimensions().contains(df.getDimension())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Returns a set of all dimensions referenced within the query filter expression
     * @return
     */
    public Set<Dimension> resolveFilterDimensions() {
        return resolveExpressionDimensions(this.filter);
    }
    
    private static Set<Dimension> resolveExpressionDimensions(IOperand operand) {
        if(null == operand) {
            return null;
        }
        HashSet<Dimension> dimensions = new HashSet<>();
        if(operand instanceof DimensionOperand) {
            DimensionField dimField = ((DimensionOperand)operand).getValue();
            if(null != dimField) {
                dimensions.add(dimField.getDimension());
            }
            return dimensions;
        }
        if(operand instanceof Expression) {
            dimensions.addAll(resolveExpressionDimensions(((Expression) operand).getLeftHandSide())); 
            dimensions.addAll(resolveExpressionDimensions(((Expression) operand).getRightHandSide())); 
            return dimensions;
        }
        return dimensions;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Query other = (Query) obj;
        return Objects.equals(this.aggregateMeasures, other.aggregateMeasures)
                && Objects.equals(this.filter, other.filter)
                && Objects.equals(this.fields, other.fields)
                && Objects.equals(this.subqueryColumnsByPosition, other.subqueryColumnsByPosition)
                && Objects.equals(this.subqueryFilter, other.subqueryFilter)
                && Objects.equals(this.having, other.having)
                && Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.joinTables, other.joinTables);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(aggregateMeasures, filter, fields, subqueryColumnsByPosition, subqueryFilter, having, joinTables, schoolId);
    }

    @Override
    public String toString() {
        return "Query{" +
                "aggregateMeasures=" + aggregateMeasures +
                ", fields=" + fields +
                ", filter=" + filter +
                ", subqueryColumnsByPosition=" + subqueryColumnsByPosition +
                ", subqueryFilter=" + subqueryFilter +
                ", joinTables=" + joinTables +
                ", having=" + having +
                ", schoolId=" + schoolId +
                '}';
    }
}
