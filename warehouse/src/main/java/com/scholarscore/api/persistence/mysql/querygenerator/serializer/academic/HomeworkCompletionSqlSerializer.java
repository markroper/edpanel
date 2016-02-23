package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.dimension.AssignmentDimension;

public class HomeworkCompletionSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectInner() {
        //TODO: currently less than 35% is considered 'incomplete' this is based on how Excel does grading, need to make this configurable
        return "if(" + toTableName() + DOT + HibernateConsts.ASSIGNMENT_TYPE_FK + " = '" + AssignmentType.HOMEWORK.name() +
                "', if(" + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS +" is null, 0," +
                " if(" + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS + "/" +
                toTableName() + DOT + HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS + " <= .35, 0, 1)), null)";
    }

    /**
     * For the feature: The purpose of this if statement is that if we wish to select homeworks
     * based on section we need to join first on the assignment table, because section_fk lives
     * on assignment not student assignment. There are no other things that live on this table
     * we would join to, but there are things that lives further up tables (Terms, courses)
     * @param dimToJoinUpon
     * @return
     */
    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {

        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        if (dimTableName.equals(HibernateConsts.SECTION_TABLE)) {
            return super.toJoinClause(Dimension.SECTION) +
                    LEFT_OUTER_JOIN + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + ON +
                    toTableName() + DOT + HibernateConsts.ASSIGNMENT_ID +
                    EQUALS +
                    HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_FK +
                    " ";
        } else {
            return LEFT_OUTER_JOIN + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + ON +
                    dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) +
                    EQUALS + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + dimTableName + FK_COL_SUFFIX +
                    " " +
                    LEFT_OUTER_JOIN + toTableName() + ON +
                    HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_FK + 
                    EQUALS + toTableName() + DOT + HibernateConsts.ASSIGNMENT_ID + " ";    }
    }

    @Override
    public String toFromClause() {
        return HibernateConsts.STUDENT_ASSIGNMENT_TABLE + " " +
                LEFT_OUTER_JOIN + toTableName() + ON +
                HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_FK + 
                EQUALS + toTableName() + DOT + HibernateConsts.ASSIGNMENT_ID + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.ASSIGNMENT_TABLE;
    }

    @Override
    public String generateMeasureFieldSql(MeasureField f, String tableAlias) throws SqlGenerationException {
        String tableName = DbMappings.MEASURE_TO_TABLE_NAME.get(f.getMeasure());
        if(null != tableAlias) {
            tableName = tableAlias;
        }
        String columnName = DbMappings.MEASURE_FIELD_TO_COL_NAME.get(f);
        //Homework dimensions (all student assignment level dimenions) obscure a join between the assignment
        //and the student_assignment tables. For this reason, fields that the user can access as if they were
        //on the student_assignment table like due date and available points, really need to be queried off the
        //assignment table itself.
        if(f.getField().equals(AssignmentDimension.DUE_DATE) ||
                f.getField().equals(AssignmentDimension.AVAILABLE_POINTS)) {
            tableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(Dimension.ASSIGNMENT);
            columnName = DbMappings.DIMENSION_TO_COL_NAME.get(new DimensionField(Dimension.ASSIGNMENT, f.getField()));
        }
        if(null == tableName || null == columnName) {
            throw new SqlGenerationException("Invalid dimension, tableName (" +
                    tableName + ") and columnName (" +
                    columnName + ") must both be non-null");
        }
        return tableName + "." + columnName;
    }
    
}
