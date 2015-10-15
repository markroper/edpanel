package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.GoalPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.goalCalculators.*;

import com.scholarscore.models.goal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cwallace on 9/17/2015.
 */
@Transactional
public class GoalJdbc implements GoalPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private StudentPersistence studentPersistence;

    private CumulativeGoalCalc cumulativeGoalCalc;
    private BehaviorGoalCalc behaviorGoalCalc;
    private AssignmentGoalCalc assignmentGoalCalc;
    private AttendanceGoalCalc attendanceGoalCalc;
    private ComplexGoalCalc complexGoalCalc;

    public void setComplexGoalCalc(ComplexGoalCalc complexGoalCalc) {
        this.complexGoalCalc = complexGoalCalc;
    }

    public void setAttendanceGoalCalc(AttendanceGoalCalc attendanceGoalCalc) {
        this.attendanceGoalCalc = attendanceGoalCalc;
    }

    public void setCumulativeGoalCalc(CumulativeGoalCalc cumulativeGoalCalc) {
        this.cumulativeGoalCalc = cumulativeGoalCalc;
    }

    public void setBehaviorGoalCalc(BehaviorGoalCalc behaviorGoalCalc) {
        this.behaviorGoalCalc = behaviorGoalCalc;
    }

    public void setAssignmentGoalCalc(AssignmentGoalCalc assignmentGoalCalc) {
        this.assignmentGoalCalc = assignmentGoalCalc;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Long createGoal(Long studentId, Goal goal) {
        injectStudent(studentId, goal);
        Goal result = hibernateTemplate.merge(goal);
        return result.getId();
    }

    private void injectStudent(long studentId, Goal goal) {
        if (null == goal.getStudent()) {
            goal.setStudent(studentPersistence.select(studentId));
        }
    }

    @Override
    public Goal select(long studentId, long goalId) {
        Goal result = hibernateTemplate.get(Goal.class, goalId);
        if (null != result) {
            return addCalculatedValue(result);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Goal> selectAll(long studentId) {
        return addCalculatedValue((Collection<Goal>) hibernateTemplate.findByNamedParam("from goal g where g.student.id = :studentId", "studentId", studentId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Goal> selectAllTeacher(long teacherId) {
        return addCalculatedValue((Collection<Goal>)hibernateTemplate.findByNamedParam("from goal g where g.teacher.id = :teacherId", "teacherId", teacherId));
    }

    @Override
    public Long replaceGoal(long studentId, long goalId, Goal goal) {
        injectStudent(studentId, goal);
        goal.setId(goalId);
        hibernateTemplate.merge(goal);
        return goalId;
    }

    @Override
    public Long delete(long studentId, long goalId) {
        Goal result = select(studentId, goalId);
        if (null != result) {
            hibernateTemplate.delete(result);
        }
        return goalId;
    }

    private Collection<Goal> addCalculatedValue(Collection<Goal> goals) {
        Collection<Goal> calculatedGoals = new ArrayList<Goal>();
        for (Goal goal : goals) {
            calculatedGoals.add(addCalculatedValue(goal));
        }
        return calculatedGoals;
    }

    private Goal addCalculatedValue(Goal goal) {
        switch (goal.getGoalType()) {
            case BEHAVIOR:
                if (goal instanceof BehaviorGoal) {
                    BehaviorGoal behaviorGoal = (BehaviorGoal)goal;
                    goal.setCalculatedValue(behaviorGoalCalc.calculateGoal(behaviorGoal));
                }
                break;
            case ASSIGNMENT:
                if (goal instanceof AssignmentGoal) {
                    AssignmentGoal assignmentGoal = (AssignmentGoal)goal;
                    goal.setCalculatedValue(assignmentGoalCalc.calculateGoal(assignmentGoal));
                }
                break;
            case CUMULATIVE_GRADE:
                if (goal instanceof CumulativeGradeGoal) {
                    CumulativeGradeGoal cumulativeGradeGoal = (CumulativeGradeGoal)goal;
                    goal.setCalculatedValue(cumulativeGoalCalc.calculateGoal(cumulativeGradeGoal));
                }
                break;
            case ATTENDANCE:
                if (goal instanceof AttendanceGoal) {
                    AttendanceGoal attendanceGoal = (AttendanceGoal)goal;
                    goal.setCalculatedValue(attendanceGoalCalc.calculateGoal(attendanceGoal));
                }
                break;
            case COMPLEX:
                if (goal instanceof ComplexGoal) {
                    ComplexGoal complexGoal = (ComplexGoal)goal;
                    goal.setCalculatedValue(complexGoalCalc.calculateGoal(complexGoal));
                }
        }
        return goal;
    }

}
