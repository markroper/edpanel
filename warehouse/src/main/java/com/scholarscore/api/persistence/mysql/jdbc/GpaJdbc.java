package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.GpaPersistence;
import com.scholarscore.models.gpa.CurrentGpa;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Student;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author markroper on 11/24/15.
 */
public class GpaJdbc implements GpaPersistence {
    private HibernateTemplate hibernateTemplate;

    /**
     * Insert the GPA for the student.  Get the current GPA for the student.  If the calc date
     * of the inserted GPA is greater than the calc day of the current, replace the current to point
     * at that which was just inserted.
     *
     * @param studentId
     * @param gpa
     * @return
     */
    @Override
    public Long insertGpa(long studentId, Gpa gpa) {
        hibernateTemplate.save(gpa);
        CurrentGpa curr = getCurrentGpaByGpaStudentId(gpa.getStudentId());
        //Handle updating the current GPA for ths student, if needed
        if(null == curr || curr.getGpa().getCalculationDate().isBefore(gpa.getCalculationDate())) {
            Student s = new Student();
            s.setId(gpa.getStudentId());
            CurrentGpa newCurrGpa = new CurrentGpa();
            newCurrGpa.setStudent(s);
            newCurrGpa.setGpa(gpa);
            if(curr != null) {
                newCurrGpa.setId(curr.getId());
            }
            hibernateTemplate.update(newCurrGpa);
        }
        return gpa.getId();
    }

    /**
     * Get the existing GPA. If the input GPA has a new calculation date, check to see if it is the current GPA
     * and no longer should be or if it isn't the current GPA and now needs to be.  Insert the value and make the
     * change to the current GPA if that is needed.
     *
     * @param studentId
     * @param gpaId
     * @param gpa
     * @return
     */
    @Override
    public Long updateGpa(long studentId, long gpaId, Gpa gpa) {
        gpa.setId(gpaId);
        CurrentGpa curr = getCurrentGpaByGpaStudentId(studentId);
        hibernateTemplate.update(gpa);
        if(null == curr) {
            CurrentGpa newCurrent = new CurrentGpa();
            newCurrent.setGpa(gpa);
            Student stud = new Student();
            stud.setId(studentId);
            newCurrent.setStudent(stud);
            hibernateTemplate.save(newCurrent);
        } else if(curr.getGpa().getCalculationDate().isBefore(gpa.getCalculationDate())) {
            CurrentGpa newCurrent = new CurrentGpa();
            newCurrent.setGpa(gpa);
            newCurrent.setId(curr.getId());
            newCurrent.setStudent(curr.getStudent());
            hibernateTemplate.update(newCurrent);
        }
        return gpa.getId();
    }

    /**
     * Return the current GPA for the student.
     *
     * @param studentId
     * @return
     */
    @Override
    public Gpa selectGpa(long studentId) {
        CurrentGpa curr = getCurrentGpaByGpaStudentId(studentId);
        if(null == curr) {
            return null;
        }
        return curr.getGpa();
    }

    /**
     * If startDate and endDate are null, return the current GPAs for the studentIds provided.
     * If startDate or endDate are not null, return all GPA calculations between the startDate and endDate. If
     * startDate is null, set no lower bound. If endDate is null, set no upper bound.
     *
     * @param studentIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<Gpa> selectStudentGpas(List<Long> studentIds, LocalDate startDate, LocalDate endDate) {
        if(null == startDate && null == endDate) {
            //Get the current GPAs for the students
            return getCurrentGpasForStudents(studentIds);
        } else {
            //Get ALL GPAs where the calculated date is between start and end
            return getAllGpasBetweenDatesForStudent(studentIds, startDate, endDate);
        }
    }

    /**
     * Delete the GPA with the ID provided.  If it was the current GPA, find the next most recent current date and
     * set that as the current date.
     * @param studentId
     * @param gpaId
     */
    @Override
    public void delete(long studentId, long gpaId) {
        hibernateTemplate.delete(gpaId);
    }

    @SuppressWarnings("unchecked")
    private List<Gpa> getCurrentGpasForStudents(List<Long> studentIds) {
        String[] params = new String[]{"studentIds" };
        Object[] paramValues = new Object[]{ studentIds };
        List<CurrentGpa> objects = (List<CurrentGpa>) hibernateTemplate.findByNamedParam(
                "from current_gpa g " +
                "join fetch g.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
                "left join fetch st.contactMethods join fetch g.gpa gpa " +
                "where st.id in (:studentIds)",
                params,
                paramValues);
        if(null == objects || objects.isEmpty()) {
            return null;
        }
        List<Gpa> gpas = new ArrayList<>();
        for(CurrentGpa curr : objects) {
            gpas.add(curr.getGpa());
        }
        return gpas;
    }

    @SuppressWarnings("unchecked")
    private List<Gpa> getAllGpasBetweenDatesForStudent(List<Long> studentIds, LocalDate start, LocalDate end) {
        String[] params = new String[]{"studentIds", "startDate", "endDate" };
        Object[] paramValues = new Object[]{ studentIds, start, end };
        List<Gpa> objects = (List<Gpa>) hibernateTemplate.findByNamedParam(
                "from gpa g where g.studentId in (:studentIds) " +
                "and g.calculationDate >= :startDate and g.calculationDate <= :endDate",
                params,
                paramValues);
        return objects;
    }

    @SuppressWarnings("unchecked")
    private CurrentGpa getCurrentGpaByGpaStudentId(long studentId) {
        String[] params = new String[]{"studentId" };
        Object[] paramValues = new Object[]{ new Long(studentId) };
        List<CurrentGpa> objects = (List<CurrentGpa>) hibernateTemplate.findByNamedParam(
                "from current_gpa g " +
                "join fetch g.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
                "left join fetch st.contactMethods join fetch g.gpa gpa " +
                "where st.id = :studentId",
                params,
                paramValues);
        if(null == objects || objects.isEmpty()) {
            return null;
        }
        return objects.get(0);
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
