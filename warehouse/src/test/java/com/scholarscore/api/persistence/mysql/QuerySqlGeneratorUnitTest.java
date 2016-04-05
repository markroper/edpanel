package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlWithParameters;
import com.scholarscore.models.goal.GoalProgress;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.SubqueryColumnRef;
import com.scholarscore.models.query.SubqueryExpression;
import com.scholarscore.models.query.bucket.AggregationBucket;
import com.scholarscore.models.query.bucket.NumericBucket;
import com.scholarscore.models.query.dimension.CourseDimension;
import com.scholarscore.models.query.dimension.GoalDimension;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.dimension.TeacherDimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.ListNumericOperand;
import com.scholarscore.models.query.expressions.operands.MeasureOperand;
import com.scholarscore.models.query.expressions.operands.NullOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operands.StringOperand;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.measure.AttendanceMeasure;
import com.scholarscore.models.query.measure.behavior.BehaviorMeasure;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Test(groups = {"unit"})
public class QuerySqlGeneratorUnitTest {

    public interface TestQuery {
        String queryName();
        Query buildQuery();
        String buildSQL();
    }

    // all queries must produce executable SQL which will be tested against an instance of the database (parameters are OK)
    @DataProvider
    public static Object[][] queriesProvider() {

        // initialize shared objects here -- anything used by multiple queries
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final Date date1;
        final Date date2;
        try {
            date1 = dateFormat.parse("01-09-2014");
            date2 = dateFormat.parse("01-09-2015");
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse date format - cannot continue!", e);
        }
        final Expression termClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.TERM, SectionDimension.ID)),
                ComparisonOperator.EQUAL,
                new NumericOperand(1));
        final Expression yearClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.YEAR, SectionDimension.ID)),
                ComparisonOperator.EQUAL,
                new NumericOperand(1));
        final Expression sectionClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                ComparisonOperator.NOT_EQUAL,
                new NumericOperand(0));
        final Expression termAndYearClause = new Expression(termClause, BinaryOperator.AND, yearClause);
        final Expression termAndYearAndSectionClause = new Expression(termAndYearClause, BinaryOperator.AND, sectionClause);
        
        final ListNumericOperand sectionList = new ListNumericOperand();
        ArrayList<Number> sections = new ArrayList<>();
        sections.add(2);
        sections.add(3);
        sectionList.setValue(sections);

        List<AggregationBucket> buckets = new ArrayList<>();
        buckets.add(new NumericBucket(0D, 1D, "0-1"));
        buckets.add(new NumericBucket(1D, 2D, "1-2"));
        buckets.add(new NumericBucket(2D, 3D, "2-3"));
        buckets.add(new NumericBucket(3D, null, "4+"));
        // done shared object initialization

        TestQuery courseGradeTestQuery = new TestQuery() {
            @Override
            public String queryName() { return "Course Grade query"; }

            @Override
            public Query buildQuery() {
                Query courseGradeQuery = new Query();
                //Define aggregate measures
                List<AggregateMeasure> measures = new ArrayList<>();
                measures.add(new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.SUM));
                courseGradeQuery.setAggregateMeasures(measures);
                //No date dimension for this query
                courseGradeQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.AGE));
                courseGradeQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY));
                courseGradeQuery.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME));
                //Create expression
                Expression whereClause = new Expression();
                Expression minBound = new Expression(
                        new DateOperand(date1),
                        ComparisonOperator.GREATER_THAN_OR_EQUAL,
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)));
                Expression maxBound = new Expression(
                        new DateOperand(date2),
                        ComparisonOperator.LESS_THAN_OR_EQUAL,
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)));
                whereClause.setLeftHandSide(minBound);
                whereClause.setOperator(BinaryOperator.AND);
                whereClause.setRightHandSide(maxBound);
                courseGradeQuery.setFilter(whereClause);
                return courseGradeQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.birth_date, student.federal_ethnicity, school.school_name, SUM(section_grade.grade) as sum_course_grade_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_section_grade ON student.student_user_fk = student_section_grade.student_fk " +
                        "LEFT OUTER JOIN section_grade ON student_section_grade.section_grade_fk = section_grade.section_grade_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = student_section_grade.section_fk \n" +
                        "LEFT OUTER JOIN school ON school.school_id = student.school_fk \n" +
                        "WHERE  ( ( '2014-09-01 00:00:00.0'  >=  section.section_start_date )  AND  ( '2015-09-01 00:00:00.0'  <=  section.section_start_date ) ) \n" +
                        "GROUP BY student.birth_date, student.federal_ethnicity, school.school_name";
            }
        };

        TestQuery assignmentGradesTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Assignment Grades query";
            }

            @Override
            public Query buildQuery() {
                Query assignmentGradesQuery = new Query();
                ArrayList<AggregateMeasure> assignmentMeasures = new ArrayList<>();
                assignmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
                assignmentGradesQuery.setAggregateMeasures(assignmentMeasures);
                assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
                Expression assignmentWhereClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                        ComparisonOperator.EQUAL,
                        new NumericOperand(4));
                assignmentGradesQuery.setFilter(assignmentWhereClause);
                return assignmentGradesQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_name, AVG(student_assignment.awarded_points / assignment.available_points) as avg_assignment_grade_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk " +
                        "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = assignment.section_fk \n" +
                        "WHERE  ( section.section_id  =  4 ) \n" +
                        "GROUP BY student.student_name";
            }
        };

        TestQuery assignmentGradesIsNullTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Assignment Grades query";
            }

            @Override
            public Query buildQuery() {
                Query assignmentGradesQuery = new Query();
                ArrayList<AggregateMeasure> assignmentMeasures = new ArrayList<>();
                assignmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
                assignmentGradesQuery.setAggregateMeasures(assignmentMeasures);
                assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
                Expression assignmentWhereClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                        ComparisonOperator.IS,
                        new NullOperand());
                assignmentGradesQuery.setFilter(assignmentWhereClause);
                return assignmentGradesQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_name, AVG(student_assignment.awarded_points / assignment.available_points) as avg_assignment_grade_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk " +
                        "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = assignment.section_fk \n" +
                        "WHERE  ( section.section_id  IS  NULL ) \n" +
                        "GROUP BY student.student_name";
            }
        };

        TestQuery assignmentGradesIsNotNullTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Assignment Grades query";
            }

            @Override
            public Query buildQuery() {
                Query assignmentGradesQuery = new Query();
                ArrayList<AggregateMeasure> assignmentMeasures = new ArrayList<>();
                assignmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
                assignmentGradesQuery.setAggregateMeasures(assignmentMeasures);
                assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
                Expression assignmentWhereClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                        ComparisonOperator.IS_NOT,
                        new NullOperand());
                assignmentGradesQuery.setFilter(assignmentWhereClause);
                return assignmentGradesQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_name, AVG(student_assignment.awarded_points / assignment.available_points) as avg_assignment_grade_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk " +
                        "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = assignment.section_fk \n" +
                        "WHERE  ( section.section_id  IS NOT  NULL ) \n" +
                        "GROUP BY student.student_name";
            }
        };

        TestQuery assignmentGradesNoDimensionsTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Assignment Grades No Dimensions query";
            }

            @Override
            public Query buildQuery() {
                Query assignmentGradesQuery = new Query();
                ArrayList<AggregateMeasure> assignmentMeasures = new ArrayList<>();
                assignmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
                assignmentGradesQuery.setAggregateMeasures(assignmentMeasures);
                return assignmentGradesQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT AVG(student_assignment.awarded_points / assignment.available_points) as avg_assignment_grade_agg \n" +
                        "FROM student_assignment LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id ";
            }
        };

        TestQuery homeworkCompletionTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Homework query with pathfinder";
            }

            @Override
            public Query buildQuery() {
                Query homeworkCompletionQuery = new Query();
                ArrayList<AggregateMeasure> homeworkMeasures = new ArrayList<>();
                homeworkMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                homeworkCompletionQuery.setAggregateMeasures(homeworkMeasures);
                homeworkCompletionQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                homeworkCompletionQuery.setFilter(termAndYearAndSectionClause);
                return homeworkCompletionQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk " +
                        "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = assignment.section_fk \n" +
                        "LEFT OUTER JOIN term ON term.term_id = section.term_fk \n" +
                        "LEFT OUTER JOIN school_year ON school_year.school_year_id = term.school_year_fk \n" +
                        "WHERE  ( ( ( term.term_id  =  1 )  AND  ( school_year.school_year_id  =  1 ) )  AND  ( section.section_id  !=  0 ) ) \n" +
                        "GROUP BY student.student_user_fk";
            }
        };
        TestQuery homeworkSectionCompletionTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Homework by Section query";
            }

            @Override
            public Query buildQuery() {
                Query homeworkSectionCompletionQuery = new Query();
                ArrayList<AggregateMeasure> homeworkSectionMeasures = new ArrayList<>();
                homeworkSectionMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                homeworkSectionCompletionQuery.setAggregateMeasures(homeworkSectionMeasures);
                homeworkSectionCompletionQuery.addField(new DimensionField(Dimension.SECTION, SectionDimension.ID));
                homeworkSectionCompletionQuery.setFilter(termAndYearAndSectionClause);
                return homeworkSectionCompletionQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT section.section_id, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM section LEFT OUTER JOIN assignment ON section.section_id = assignment.section_fk LEFT OUTER JOIN student_assignment ON assignment.assignment_id = student_assignment.assignment_fk \n" +
                        "LEFT OUTER JOIN term ON term.term_id = section.term_fk \n" +
                        "LEFT OUTER JOIN school_year ON school_year.school_year_id = term.school_year_fk \n" +
                        "WHERE  ( ( ( term.term_id  =  1 )  AND  ( school_year.school_year_id  =  1 ) )  AND  ( section.section_id  !=  0 ) ) \n" +
                        "GROUP BY section.section_id";
            }
        };
        TestQuery studentAttendanceQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Student Attendance query";
            }

            @Override
            public Query buildQuery() {
                Query attendanceQuery = new Query();
                ArrayList<AggregateMeasure> attendanceMeasures = new ArrayList<>();
                attendanceMeasures.add(new AggregateMeasure(Measure.ATTENDANCE, AggregateFunction.SUM));
                attendanceQuery.setAggregateMeasures(attendanceMeasures);
                attendanceQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                Expression greaterThanDate = new Expression(
                        new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                        ComparisonOperator.GREATER_THAN_OR_EQUAL,
                        new DateOperand(date1));
                Expression lessThanDate = new Expression(
                        new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                        ComparisonOperator.LESS_THAN_OR_EQUAL,
                        new DateOperand(date2));
                Expression attendanceDateRangeExpression = new Expression(greaterThanDate, BinaryOperator.AND, lessThanDate);
                attendanceQuery.setFilter(attendanceDateRangeExpression);
                return attendanceQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, SUM(if(attendance.attendance_status in ('ABSENT'), 1, null)) as sum_attendance_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN attendance ON student.student_user_fk = attendance.student_fk " +
                        "LEFT OUTER JOIN school_day ON attendance.school_day_fk = school_day.school_day_id \n" +
                        "WHERE  ( ( school_day.school_day_date  >=  '2014-09-01 00:00:00.0' )  AND  ( school_day.school_day_date  <=  '2015-09-01 00:00:00.0' ) ) \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery schoolAttendanceQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "School Attendance query";
            }

            @Override
            public Query buildQuery() {
                Query attendanceQuery = new Query();
                ArrayList<AggregateMeasure> attendanceMeasures = new ArrayList<>();
                attendanceMeasures.add(new AggregateMeasure(Measure.ATTENDANCE, AggregateFunction.SUM));
                attendanceQuery.setAggregateMeasures(attendanceMeasures);
                attendanceQuery.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.ID));
                Expression greaterThanDate = new Expression(
                        new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                        ComparisonOperator.GREATER_THAN_OR_EQUAL,
                        new DateOperand(date1));
                Expression lessThanDate = new Expression(
                        new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                        ComparisonOperator.LESS_THAN_OR_EQUAL,
                        new DateOperand(date2));
                Expression attendanceDateRangeExpression = new Expression(greaterThanDate, BinaryOperator.AND, lessThanDate);
                attendanceQuery.setFilter(attendanceDateRangeExpression);
                return attendanceQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_id, SUM(if(attendance.attendance_status in ('ABSENT'), 1, null)) as sum_attendance_agg \n" +
                        "FROM school LEFT OUTER JOIN school_day ON school.school_id = school_day.school_fk LEFT OUTER JOIN attendance ON school_day.school_day_id = attendance.school_day_fk \n" +
                        "WHERE  ( ( school_day.school_day_date  >=  '2014-09-01 00:00:00.0' )  AND  ( school_day.school_day_date  <=  '2015-09-01 00:00:00.0' ) ) \n" +
                        "GROUP BY school.school_id";
            }
        };

        TestQuery sectionAbsenceTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Absence by Section query";
            }

            @Override
            public Query buildQuery() {
                Query sectionAbsenceQuery = new Query();
                ArrayList<AggregateMeasure> sectionAbsenceMeasures = new ArrayList<>();
                sectionAbsenceMeasures.add(new AggregateMeasure(Measure.SECTION_ABSENCE, AggregateFunction.COUNT));
                sectionAbsenceQuery.setAggregateMeasures(sectionAbsenceMeasures);
                sectionAbsenceQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                sectionAbsenceQuery.addField(new DimensionField(Dimension.SECTION, SectionDimension.ID));
                Expression sectionAbsenceClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                        ComparisonOperator.IN,
                        sectionList);
                sectionAbsenceQuery.setFilter(sectionAbsenceClause);
                return sectionAbsenceQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, section.section_id, COUNT(if(attendance.attendance_status in ('ABSENT') AND attendance.attendance_type = 'SECTION', 1, 0)) as count_section_absence_agg \n" +
                        "FROM student LEFT OUTER JOIN attendance ON student.student_user_fk = attendance.student_fk " +
                        "LEFT OUTER JOIN school_day ON attendance.school_day_fk = school_day.school_day_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = attendance.section_fk \n" +
                        "WHERE  ( section.section_id  IN  (2,3) ) \n" +
                        "GROUP BY student.student_user_fk, section.section_id";
            }
        };
        TestQuery sectionTardyTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Tardy by Section query";
            }

            @Override
            public Query buildQuery() {
                Query sectionTardyQuery = new Query();
                ArrayList<AggregateMeasure> sectionTardyMeasures = new ArrayList<>();
                sectionTardyMeasures.add(new AggregateMeasure(Measure.SECTION_TARDY, AggregateFunction.COUNT));
                sectionTardyQuery.setAggregateMeasures(sectionTardyMeasures);
                sectionTardyQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                sectionTardyQuery.addField(new DimensionField(Dimension.SECTION, SectionDimension.ID));
                Expression sectionTardyClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)),
                        ComparisonOperator.IN,
                        sectionList);
                sectionTardyQuery.setFilter(sectionTardyClause);
                return sectionTardyQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, section.section_id, COUNT(if(attendance.attendance_status in ('TARDY') AND attendance.attendance_type = 'SECTION', 1, 0)) as count_section_tardy_agg \n" +
                        "FROM student LEFT OUTER JOIN attendance ON student.student_user_fk = attendance.student_fk " +
                        "LEFT OUTER JOIN school_day ON attendance.school_day_fk = school_day.school_day_id \n" +
                        "LEFT OUTER JOIN section ON section.section_id = attendance.section_fk \n" +
                        "WHERE  ( section.section_id  IN  (2,3) ) \n" +
                        "GROUP BY student.student_user_fk, section.section_id";
            }
        };

        TestQuery sectionTardyWithoutDimensionTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Tardy by Section without dimension query";
            }

            @Override
            public Query buildQuery() {
                Query sectionTardyQuery = new Query();
                ArrayList<AggregateMeasure> sectionTardyMeasures = new ArrayList<>();
                sectionTardyMeasures.add(new AggregateMeasure(Measure.SECTION_TARDY, AggregateFunction.COUNT));
                sectionTardyQuery.setAggregateMeasures(sectionTardyMeasures);
                return sectionTardyQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT COUNT(if(attendance.attendance_status in ('TARDY') AND attendance.attendance_type = 'SECTION', 1, 0)) as count_section_tardy_agg \n" +
                        "FROM attendance ";
            }
        };

        TestQuery dailyTardyTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Daily Tardy query";
            }

            @Override
            public Query buildQuery() {
                Query dailyTardyQuery = new Query();
                ArrayList<AggregateMeasure> sectionTardyMeasures = new ArrayList<>();
                sectionTardyMeasures.add(new AggregateMeasure(Measure.TARDY, AggregateFunction.COUNT));
                dailyTardyQuery.setAggregateMeasures(sectionTardyMeasures);
                dailyTardyQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                return dailyTardyQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, COUNT(if(attendance.attendance_status in ('TARDY') AND attendance.attendance_type = 'DAILY', 1, 0)) as count_tardy_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN attendance ON student.student_user_fk = attendance.student_fk " +
                        "LEFT OUTER JOIN school_day ON attendance.school_day_fk = school_day.school_day_id \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery dailyAbsenceTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Daily Absence query";
            }

            @Override
            public Query buildQuery() {
                Query dailyAbsenceQuery = new Query();
                ArrayList<AggregateMeasure> sectionTardyMeasures = new ArrayList<>();
                sectionTardyMeasures.add(new AggregateMeasure(Measure.ABSENCE, AggregateFunction.COUNT));
                dailyAbsenceQuery.setAggregateMeasures(sectionTardyMeasures);
                dailyAbsenceQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                return dailyAbsenceQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, COUNT(if(attendance.attendance_status in ('ABSENT') AND attendance.attendance_type = 'DAILY', 1, 0)) as count_absence_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN attendance ON student.student_user_fk = attendance.student_fk " +
                        "LEFT OUTER JOIN school_day ON attendance.school_day_fk = school_day.school_day_id \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery demeritTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Demerit test query";
            }

            @Override
            public Query buildQuery() {
                Query behaviorQuery = new Query();
                ArrayList<AggregateMeasure> behaviorMeasures = new ArrayList<>();
                behaviorMeasures.add(new AggregateMeasure(Measure.DEMERIT, AggregateFunction.SUM));
                behaviorQuery.setAggregateMeasures(behaviorMeasures);
                behaviorQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                Expression studentIdClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.STUDENT, StudentDimension.ID)),
                        ComparisonOperator.EQUAL,
                        new NumericOperand(1L));
                Date afterDate = null;
                try {
                    afterDate = dateFormat.parse("01-09-2014");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Expression dateClause = new Expression(
                        new MeasureOperand(new MeasureField(Measure.DEMERIT, BehaviorMeasure.DATE)),
                        ComparisonOperator.GREATER_THAN,
                        new DateOperand(afterDate));
                Expression topClause = new Expression(dateClause, BinaryOperator.AND, studentIdClause);
                behaviorQuery.setFilter(topClause);
                return behaviorQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, SUM(if(behavior.category = 'DEMERIT', 1, 0)) as sum_demerit_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN behavior ON student.student_user_fk = behavior.student_fk \n" +
                        "WHERE  ( ( behavior.date  >  '2014-09-01 00:00:00.0' )  AND  ( student.student_user_fk  =  1 ) ) \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery demeritWithoutDimensionTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Demerit w/ only measure, no dimension test query";
            }

            @Override
            public Query buildQuery() {
                Query behaviorQuery = new Query();
                ArrayList<AggregateMeasure> behaviorMeasures = new ArrayList<>();
                behaviorMeasures.add(new AggregateMeasure(Measure.DEMERIT, AggregateFunction.SUM));
                behaviorQuery.setAggregateMeasures(behaviorMeasures);
                return behaviorQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT SUM(if(behavior.category = 'DEMERIT', 1, 0)) as sum_demerit_agg \n" +
                        "FROM behavior ";
            }
        };


        TestQuery detentionWithoutDimensionTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Detention w/ only measure, no dimension test query";
            }

            @Override
            public Query buildQuery() {
                Query behaviorQuery = new Query();
                ArrayList<AggregateMeasure> behaviorMeasures = new ArrayList<>();
                behaviorMeasures.add(new AggregateMeasure(Measure.DETENTION, AggregateFunction.SUM));
                behaviorQuery.setAggregateMeasures(behaviorMeasures);
                return behaviorQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT SUM(if(behavior.category = 'DETENTION', 1, 0)) as sum_detention_agg \n" +
                        "FROM behavior ";
            }
        };

        TestQuery meritTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Merit test query";
            }

            @Override
            public Query buildQuery() {
                Query behaviorQuery = new Query();
                ArrayList<AggregateMeasure> behaviorMeasures = new ArrayList<>();
                behaviorMeasures.add(new AggregateMeasure(Measure.MERIT, AggregateFunction.SUM));
                behaviorQuery.setAggregateMeasures(behaviorMeasures);
                behaviorQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                Expression studentIdClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.STUDENT, StudentDimension.ID)),
                        ComparisonOperator.EQUAL,
                        new NumericOperand(1L));
                Date afterDate = null;
                try {
                    afterDate = dateFormat.parse("01-09-2014");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Expression dateClause = new Expression(
                        new MeasureOperand(new MeasureField(Measure.DEMERIT, BehaviorMeasure.DATE)),
                        ComparisonOperator.GREATER_THAN,
                        new DateOperand(afterDate));
                Expression topClause = new Expression(dateClause, BinaryOperator.AND, studentIdClause);
                behaviorQuery.setFilter(topClause);
                return behaviorQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, SUM(if(behavior.category = 'MERIT', 1, 0)) as sum_merit_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN behavior ON student.student_user_fk = behavior.student_fk \n" +
                        "WHERE  ( ( behavior.date  >  '2014-09-01 00:00:00.0' )  AND  ( student.student_user_fk  =  1 ) ) \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery demeritWithStaffTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Demerit with staff test query";
            }

            @Override
            public Query buildQuery() {
                Query behaviorQuery = new Query();
                ArrayList<AggregateMeasure> behaviorMeasures = new ArrayList<>();
                behaviorMeasures.add(new AggregateMeasure(Measure.DEMERIT, AggregateFunction.SUM));
                behaviorQuery.setAggregateMeasures(behaviorMeasures);
                behaviorQuery.addField(new DimensionField(Dimension.TEACHER, TeacherDimension.ID));
                return behaviorQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT staff.staff_user_fk, SUM(if(behavior.category = 'DEMERIT', 1, 0)) as sum_demerit_agg \n" +
                        "FROM staff " +
                        "LEFT OUTER JOIN behavior ON staff.staff_user_fk = behavior.staff_fk \n" +
                        "GROUP BY staff.staff_user_fk";
            }
        };

        TestQuery schoolNameTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "School name query";
            }

            @Override
            public Query buildQuery() {
                // this test builds a query to get the school name for the school with id 1.
                // this requires support for a query with NO aggregate function 
                Query schoolNameQuery = new Query();
                schoolNameQuery.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME));

                NumericOperand schoolId = new NumericOperand(1);
                Expression schoolNameClause = new Expression(
                        new DimensionOperand(new DimensionField(Dimension.SCHOOL, SectionDimension.ID)),
                        ComparisonOperator.EQUAL,
                        schoolId);
                schoolNameQuery.setFilter(schoolNameClause);
                return schoolNameQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_name \n" +
                        "FROM school \n" +
                        "WHERE  ( school.school_id  =  1 ) \n" +
                        "GROUP BY school.school_name";
            }
        };
        TestQuery gpaBucketTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "GPA with buckets";
            }

            @Override
            public Query buildQuery() {
                Query gpaBucketQuery = new Query();
                ArrayList<AggregateMeasure> gpaMeasures = new ArrayList<>();
                gpaBucketQuery.setAggregateMeasures(gpaMeasures);
                AggregateMeasure gpaMeasure = new AggregateMeasure(Measure.GPA, AggregateFunction.COUNT);
                List<AggregationBucket> buckets = new ArrayList<>();
                buckets.add(new NumericBucket(0D, 1D, "0-1"));
                buckets.add(new NumericBucket(1D, 2D, "1-2"));
                buckets.add(new NumericBucket(2D, 3D, "2-3"));
                buckets.add(new NumericBucket(3D, null, "4+"));
                gpaMeasure.setBuckets(buckets);
                gpaMeasures.add(gpaMeasure);
                return gpaBucketQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT CASE \n" +
                        "WHEN gpa.gpa_score >= 0.0 AND gpa.gpa_score < 1.0 THEN '0\\-1'\n" +
                        "WHEN gpa.gpa_score >= 1.0 AND gpa.gpa_score < 2.0 THEN '1\\-2'\n" +
                        "WHEN gpa.gpa_score >= 2.0 AND gpa.gpa_score < 3.0 THEN '2\\-3'\n" +
                        "WHEN gpa.gpa_score >= 3.0 THEN '4\\+'\n" +
                        "ELSE NULL \n" +
                        "END as count_gpa_group, COUNT(gpa.gpa_score) as count_gpa_agg \n" +
                        "FROM gpa \n" +
                        "GROUP BY count_gpa_group";
            }
        };
        TestQuery currGpaTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Current GPA with buckets";
            }

            @Override
            public Query buildQuery() {
                Query currGpaQuery = new Query();
                AggregateMeasure currGpaMeasure = new AggregateMeasure(Measure.CURRENT_GPA, AggregateFunction.COUNT);
                currGpaMeasure.setBuckets(buckets);
                ArrayList<AggregateMeasure> currGpaMeasures = new ArrayList<>();
                currGpaMeasures.add(currGpaMeasure);
                currGpaQuery.setAggregateMeasures(currGpaMeasures);
                return currGpaQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT CASE \n" +
                        "WHEN gpa.gpa_score >= 0.0 AND gpa.gpa_score < 1.0 THEN '0\\-1'\n" +
                        "WHEN gpa.gpa_score >= 1.0 AND gpa.gpa_score < 2.0 THEN '1\\-2'\n" +
                        "WHEN gpa.gpa_score >= 2.0 AND gpa.gpa_score < 3.0 THEN '2\\-3'\n" +
                        "WHEN gpa.gpa_score >= 3.0 THEN '4\\+'\n" +
                        "ELSE NULL \n" +
                        "END as count_current_gpa_group, COUNT(gpa.gpa_score) as count_current_gpa_agg \n" +
                        "FROM current_gpa INNER JOIN gpa ON gpa.gpa_id = current_gpa.gpa_fk \n" +
                        "GROUP BY count_current_gpa_group";
            }
        };
        TestQuery courseGradesBucketedTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Course grade buckets";
            }

            @Override
            public Query buildQuery() {
                Query courseGradesBucketed = new Query();
                AggregateMeasure gradeBucketMeasure = new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.COUNT);
                courseGradesBucketed.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                ArrayList<AggregateMeasure> gradeMeasures = new ArrayList<>();
                gradeMeasures.add(gradeBucketMeasure);
                courseGradesBucketed.setAggregateMeasures(gradeMeasures);
                List<AggregationBucket> gradeBuckets = new ArrayList<>();
                gradeBuckets.add(new NumericBucket(70D, null, "pass"));
                gradeBuckets.add(new NumericBucket(null, 70D, "fail"));
                gradeBucketMeasure.setBuckets(gradeBuckets);


                List<SubqueryColumnRef> wrapperFields = new ArrayList<>();
                wrapperFields.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
                wrapperFields.add(new SubqueryColumnRef(1, null));

                courseGradesBucketed.setSubqueryColumnsByPosition(wrapperFields);
                List<SubqueryExpression> superFilter = new ArrayList<>();
                superFilter.add(new SubqueryExpression(2, ComparisonOperator.EQUAL, new StringOperand("fail")));
                courseGradesBucketed.setSubqueryFilter(superFilter);
                return courseGradesBucketed;
            }

            @Override
            public String buildSQL() {
                return "SELECT COUNT(*), subq_1.count_course_grade_agg \n" +
                        "FROM (\n" +
                        "SELECT student.student_user_fk, CASE \n" +
                        "WHEN section_grade.grade >= 70.0 THEN 'pass'\n" +
                        "WHEN section_grade.grade < 70.0 THEN 'fail'\n" +
                        "ELSE NULL \n" +
                        "END as count_course_grade_group, COUNT(section_grade.grade) as count_course_grade_agg \n" +
                        "FROM student LEFT OUTER JOIN student_section_grade ON student.student_user_fk = student_section_grade.student_fk LEFT OUTER JOIN section_grade ON student_section_grade.section_grade_fk = section_grade.section_grade_id \n" +
                        "GROUP BY student.student_user_fk, count_course_grade_group\n" +
                        ") as subq_1 \n" +
                        "\n" +
                        "WHERE subq_1.count_course_grade_group =  'fail'  GROUP BY subq_1.count_course_grade_agg";
            }
        };

        TestQuery requiresMultipleJoinsTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Multiple join table test query";
            }

            @Override
            public Query buildQuery() {
                Query query = new Query();
                ArrayList<AggregateMeasure> aggregateMeasures = new ArrayList<>();
                aggregateMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                query.setAggregateMeasures(aggregateMeasures);
                // Our selection of fields here is deliberate. Each of these tables has at least one
                // mapping to another.
                query.addField(new DimensionField(Dimension.SCHOOL, SectionDimension.ID));
                query.addField(new DimensionField(Dimension.COURSE, CourseDimension.NAME));
                query.addField(new DimensionField(Dimension.SECTION, SectionDimension.NAME));
                return query;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_id, course.course_name, section.section_name, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM section " +
                        "LEFT OUTER JOIN assignment ON section.section_id = assignment.section_fk " +
                        "LEFT OUTER JOIN student_assignment ON assignment.assignment_id = student_assignment.assignment_fk \n" +
                        "LEFT OUTER JOIN course ON course.course_id = section.course_fk \n" +
                        "LEFT OUTER JOIN school ON school.school_id = course.school_fk \n" +
                        "GROUP BY school.school_id, course.course_name, section.section_name";
            }
        };

        TestQuery queryIncludingMultipleTablesUsingHints = new TestQuery() {
            @Override
            public String queryName() {
                return "Intermediate Tables with Hints query";
            }

            @Override
            public Query buildQuery() {
                Query query = new Query();
                ArrayList<AggregateMeasure> aggregateMeasures = new ArrayList<>();
                aggregateMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                query.setAggregateMeasures(aggregateMeasures);
                query.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME));
                query.addJoinTable(Dimension.SECTION);
                query.addJoinTable(Dimension.COURSE);
                return query;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_name, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM section " + 
                        "LEFT OUTER JOIN assignment ON section.section_id = assignment.section_fk " + 
                        "LEFT OUTER JOIN student_assignment ON assignment.assignment_id = student_assignment.assignment_fk \n" +
                        "LEFT OUTER JOIN course ON course.course_id = section.course_fk \n" +
                        "LEFT OUTER JOIN school ON school.school_id = course.school_fk \n" +
                        "GROUP BY school.school_name";
            }
        };

        TestQuery queryIncludingMultipleTablesPathFinder = new TestQuery() {
            @Override
            public String queryName() {
                return "Intermediate Tables without hints (automatic pathfinding) query";
            }

            @Override
            public Query buildQuery() {
                Query query = new Query();
                ArrayList<AggregateMeasure> aggregateMeasures = new ArrayList<>();
                aggregateMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                query.setAggregateMeasures(aggregateMeasures);
                query.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME));
                return query;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_name, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM student LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "LEFT OUTER JOIN school ON school.school_id = student.school_fk \n" +
                        "GROUP BY school.school_name";
            }
        };
        
        /*
            select count(*), num_grades
            from (
                SELECT
                    student.student_user_fk,
                    CASE
                        WHEN section_grade.grade >= 70.0 THEN 'pass'
                        WHEN section_grade.grade < 70.0 THEN 'fail'
                        ELSE NULL
                    END as count_course_grade_group,
                    COUNT(section_grade.grade) as num_grades
                FROM student
                    LEFT OUTER JOIN student_section_grade ON student.student_user_fk = student_section_grade.student_fk
                    LEFT OUTER JOIN section_grade ON section_grade.section_grade_id = student_section_grade.section_grade_fk
                where student.school_fk = 1
                GROUP BY student.student_user_fk, count_course_grade_group
            ) as subq
            where subq.count_course_grade_group = 'fail'
            group by num_grades
         */

        /*
             SELECT COUNT(*), subq_1.sum_referral_agg
             FROM (
                 SELECT
                    student.student_user_fk,
                    SUM(if(behavior.category = 'REFERRAL', 1, 0)) as sum_referral_agg
                 FROM student
                 LEFT OUTER JOIN behavior ON student.student_user_fk = behavior.student_fk
                 GROUP BY student.student_user_fk
             ) as subq_1
             GROUP BY subq_1.sum_referral_agg
         */

        TestQuery referralTestQuery = new TestQuery() {

            @Override
            public String queryName() {
                return "Referral with subq";
            }

            @Override
            public Query buildQuery() {
                Query referralQuery = new Query();
                AggregateMeasure referralMeasure = new AggregateMeasure(Measure.REFERRAL, AggregateFunction.SUM);
                List<AggregateMeasure> referrals = new ArrayList<>();
                referrals.add(referralMeasure);
                referralQuery.setAggregateMeasures(referrals);
                referralQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                List<SubqueryColumnRef> referralWrappers = new ArrayList<>();
                referralWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
                referralWrappers.add(new SubqueryColumnRef(1, null));
                referralQuery.setSubqueryColumnsByPosition(referralWrappers);
                return referralQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT COUNT(*), subq_1.sum_referral_agg \n" +
                        "FROM (\n" +
                        "SELECT student.student_user_fk, SUM(if(behavior.category = 'REFERRAL', 1, null)) as sum_referral_agg \n" +
                        "FROM student LEFT OUTER JOIN behavior ON student.student_user_fk = behavior.student_fk \n" +
                        "GROUP BY student.student_user_fk\n" +
                        ") as subq_1 \n" +
                        " GROUP BY subq_1.sum_referral_agg";
            }
        };

        TestQuery currGpaBySchoolTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Current GPA with buckets - One School";
            }

            @Override
            public Query buildQuery() {
                List<AggregationBucket> buckets = new ArrayList<>();
                buckets.add(new NumericBucket(0D, 1D, "0-1"));
                buckets.add(new NumericBucket(1D, 2D, "1-2"));
                buckets.add(new NumericBucket(2D, 3D, "2-3"));
                buckets.add(new NumericBucket(3D, null, "4+"));

                Query currGpaQuery = new Query();
                AggregateMeasure currGpaMeasure = new AggregateMeasure(Measure.CURRENT_GPA, AggregateFunction.COUNT);
                currGpaMeasure.setBuckets(buckets);
                ArrayList<AggregateMeasure> currGpaMeasures = new ArrayList<>();
                currGpaMeasures.add(currGpaMeasure);
                currGpaQuery.setAggregateMeasures(currGpaMeasures);
                currGpaQuery.addField(new DimensionField(Dimension.SCHOOL, SectionDimension.ID));
                return currGpaQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT school.school_id, CASE \n" +
                        "WHEN gpa.gpa_score >= 0.0 AND gpa.gpa_score < 1.0 THEN '0\\-1'\n" +
                        "WHEN gpa.gpa_score >= 1.0 AND gpa.gpa_score < 2.0 THEN '1\\-2'\n" +
                        "WHEN gpa.gpa_score >= 2.0 AND gpa.gpa_score < 3.0 THEN '2\\-3'\n" +
                        "WHEN gpa.gpa_score >= 3.0 THEN '4\\+'\n" +
                        "ELSE NULL \n" +
                        "END as count_current_gpa_group, COUNT(gpa.gpa_score) as count_current_gpa_agg \n" +
                        "FROM student LEFT OUTER JOIN gpa ON student.student_user_fk = gpa.student_fk INNER JOIN current_gpa ON gpa.gpa_id = current_gpa.gpa_fk\n" +
                        "LEFT OUTER JOIN school ON school.school_id = student.school_fk \n" +
                        "GROUP BY school.school_id, count_current_gpa_group";
            }
        };

        TestQuery goalTest = new TestQuery() {

            @Override
            public String queryName() {
                return "Count of achieved goals bucketed by week";
            }

            @Override
            public Query buildQuery() {
                Query goalQuery = new Query();
                AggregateMeasure goalMeasure = new AggregateMeasure(Measure.GOAL, AggregateFunction.COUNT);
                List<AggregateMeasure> goals = new ArrayList<>();
                goals.add(goalMeasure);
                goalQuery.setAggregateMeasures(goals);
                goalQuery.addField(new DimensionField(Dimension.GOAL, GoalDimension.TYPE));
                DimensionField df = new DimensionField(Dimension.GOAL, GoalDimension.START_DATE);
                df.setBucketAggregation(AggregateFunction.YEARWEEK);
                goalQuery.addField(df);
                Expression ex = new Expression(new DimensionOperand(
                        new DimensionField(Dimension.GOAL, GoalDimension.PROGRESS)),
                        ComparisonOperator.EQUAL,
                        new StringOperand(GoalProgress.UNMET.name()));
                goalQuery.setFilter(ex);
                return goalQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT goal.goal_type, YEARWEEK(goal.start_date) as start_date_YEARWEEK, COUNT(*) as count_goal_agg \n" +
                        "FROM goal \n" +
                        "WHERE  ( goal.progress  =  'UNMET' ) \n" +
                        "GROUP BY goal.goal_type, YEARWEEK(goal.start_date)";
            }
        };

        TestQuery homeworkCompletionByStudentPathfinderTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Homework completion by student (pathfinder) query";
            }

            @Override
            public Query buildQuery() {
                Query homeworkCompletionQuery = new Query();
                ArrayList<AggregateMeasure> homeworkMeasures = new ArrayList<>();
                homeworkMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                homeworkCompletionQuery.setAggregateMeasures(homeworkMeasures);
                homeworkCompletionQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
                return homeworkCompletionQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT student.student_user_fk, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM student " +
                        "LEFT OUTER JOIN student_assignment ON student.student_user_fk = student_assignment.student_fk " +
                        "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id \n" +
                        "GROUP BY student.student_user_fk";
            }
        };

        TestQuery homeworkCompletionByTermPathfinderTestQuery = new TestQuery() {
            @Override
            public String queryName() {
                return "Homework completion by term (pathfinder) query";
            }

            @Override
            public Query buildQuery() {
                Query homeworkCompletionQuery = new Query();
                ArrayList<AggregateMeasure> homeworkMeasures = new ArrayList<>();
                homeworkMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
                homeworkCompletionQuery.setAggregateMeasures(homeworkMeasures);
                homeworkCompletionQuery.addField(new DimensionField(Dimension.TERM, StudentDimension.ID));
                return homeworkCompletionQuery;
            }

            @Override
            public String buildSQL() {
                return "SELECT term.term_id, AVG(if(assignment.type_fk = 'HOMEWORK', if(student_assignment.awarded_points is null, 0, if(student_assignment.awarded_points/assignment.available_points <= .35, 0, 1)), null)) as avg_hw_completion_agg \n" +
                        "FROM section " + 
                        "LEFT OUTER JOIN assignment ON section.section_id = assignment.section_fk " +
                        "LEFT OUTER JOIN student_assignment ON assignment.assignment_id = student_assignment.assignment_fk \n" +
                        "LEFT OUTER JOIN term ON term.term_id = section.term_fk \n" +
                        "GROUP BY term.term_id";
            }
        };

        Object[][] allTests = new Object[][] {
                { courseGradeTestQuery },
                { assignmentGradesTestQuery },
                { assignmentGradesNoDimensionsTestQuery },
                { homeworkCompletionTestQuery },
                { homeworkSectionCompletionTestQuery },
                { studentAttendanceQuery },
                { schoolAttendanceQuery },
                { sectionAbsenceTestQuery },
                { sectionTardyTestQuery },
                { sectionTardyWithoutDimensionTestQuery },
                { dailyTardyTestQuery },
                { dailyAbsenceTestQuery },
                { demeritTestQuery },
                { meritTestQuery },
                { demeritWithStaffTestQuery },
                { demeritWithoutDimensionTestQuery },
                { detentionWithoutDimensionTestQuery },
                { schoolNameTestQuery },
                { gpaBucketTestQuery },
                { currGpaTestQuery },
//                { courseGradesBucketedTestQuery },
                { requiresMultipleJoinsTestQuery },
                { queryIncludingMultipleTablesUsingHints },
                { referralTestQuery },
                { queryIncludingMultipleTablesPathFinder },
                { currGpaBySchoolTestQuery },
                { assignmentGradesIsNullTestQuery },
                { assignmentGradesIsNotNullTestQuery },
                { homeworkCompletionByStudentPathfinderTestQuery },
                { homeworkCompletionByTermPathfinderTestQuery },
				{ goalTest }
        };

        return allTests;
    }

    @Test(dataProvider = "queriesProvider")
    public void toSqlTest(TestQuery testQuery) {
        String msg = testQuery.queryName();
        Query q = testQuery.buildQuery();
        String expectedSql = testQuery.buildSQL();

        SqlWithParameters sql = null;
        try {
            sql = QuerySqlGenerator.generate(q);
        } catch (SqlGenerationException e) {
            Assert.fail(msg + " failed with exception " + e.getMessage());
        }
        Assert.assertNotNull(sql, msg);

        String sqlString = getSQLQueryWithPopulatedParams(sql);
        Assert.assertEquals(sqlString, expectedSql, msg + " for test case " + testQuery.queryName());
    }

    protected static String getSQLQueryWithPopulatedParams(SqlWithParameters query) {
        String querySql = query.getSql();
        if (query.getParams() != null) {
            for (String paramKey : query.getParams().keySet()) {
                Object value = query.getParams().get(paramKey);
                if (value instanceof String) {
                    if (paramKey != null) {
                        querySql = querySql.replace(":" + paramKey, "'" + value + "'");
                    }
                }
                // if non-string parameters are added later, they will need to be handled here
            }
        }
        return querySql;
    }
}
