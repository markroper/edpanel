package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.AttendancePersistence;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.goal.CalculatableAttendance;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Created by cwallace on 10/5/2015.
 */
public class AttendanceGoalCalc implements GoalCalc<CalculatableAttendance> {

    private AttendancePersistence attendancePersistence;

    public void setAttendancePersistence(AttendancePersistence attendancePersistence) {
        this.attendancePersistence = attendancePersistence;
    }

    public Double calculateGoal(CalculatableAttendance goal) {

        Collection<Attendance> attendances;
        if (null != goal.getSection()) {
            attendances = attendancePersistence.selectAttendanceForSection(goal.getStudent().getId(),goal.getSection().getId());
        } else {
            attendances = attendancePersistence.selectAllDailyAttendance(goal.getStudent().getId());
        }

        Double missedClasses = 0d;
        //TODO SHOULD FINISHED GOALS BE PRESERVED WITH A FINAL VALUE SO WE HAVE LESS QUERYING;
        for (Attendance dayAttendance : attendances) {
            if (dayAttendance.getSchoolDay().getDate().isAfter(goal.getStartDate()) ||
                    dayAttendance.getSchoolDay().getDate().equals(goal.getStartDate())){
                //We have atleast started this goal...

                //We don't always have an end date to goals. If they are teacher complete stuff
                LocalDate endDate = goal.getEndDate();
                if (null == endDate) {
                    //Teacher is going to close off this goal so we don't care what the end date is
                    missedClasses += 1;
                } else {
                    if (dayAttendance.getSchoolDay().getDate().isBefore(goal.getEndDate()) ||
                            dayAttendance.getSchoolDay().getDate().equals(goal.getEndDate())) {
                        //This is in the valid timeframe
                        missedClasses += 1;
                    }
                }
            }

        }
        return missedClasses;
    }
}
