package com.scholarscore.api.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.persistence.mysql.SectionPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentSectionGrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by cwallace on 9/16/2015.
 */
public class SectionManagerImpl implements SectionManager {

    SectionPersistence sectionPersistence;

    PersistenceManager pm;


    private static final String SECTION = "section";

    public void setSectionPersistence(SectionPersistence sectionPersistence) {
        this.sectionPersistence = sectionPersistence;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    /**
     * Returns all sections in a given school term, with all section assignments
     * and enrolled students populated on the instance.
     *
     * @param schoolId
     * @param yearId
     * @param termId
     * @return
     */
    @Override
    public ServiceResponse<Collection<Section>> getAllSections(long schoolId, long yearId, long termId) {
        StatusCode code = pm.getTermManager().termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        Collection<Section> sections = sectionPersistence.selectAll(termId);
        for(Section s : sections) {
            Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(s.getId());
            if(null != students && !students.isEmpty()) {
                s.setEnrolledStudents(new ArrayList<Student>(students));
            }
            Collection<Assignment> assignments = pm.getAssignmentPersistence().selectAll(s.getId());
            if(null != assignments && !assignments.isEmpty()) {
                s.setAssignments(new ArrayList<Assignment>(assignments));
            }
        }
        return new ServiceResponse<Collection<Section>>(sections);
    }

    @Override
    public ServiceResponse<Collection<Section>> getAllSections(long studentId,
                                                               long schoolId, long yearId, long termId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        code = pm.getTermManager().termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        Collection<Section> sections = sectionPersistence.selectAllSectionForStudent(termId, studentId);
        for(Section s : sections) {
            Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(s.getId());
            if(null != students && !students.isEmpty()) {
                s.setEnrolledStudents(new ArrayList<Student>(students));
            }
            Collection<Assignment> assignments = pm.getAssignmentPersistence().selectAll(s.getId());
            if(null != assignments && !assignments.isEmpty()) {
                s.setAssignments(new ArrayList<Assignment>(assignments));
            }
        }
        return new ServiceResponse<Collection<Section>>(sections);
    }

    @Override
    public StatusCode sectionExists(long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = pm.getTermManager().termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return code;
        }
        Section section = sectionPersistence.select(termId, sectionId);
        if(null == section) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SECTION, sectionId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    /**
     * Returns a section with its enrolled students and section assignments populated
     *
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @return
     */
    @Override
    public ServiceResponse<Section> getSection(long schoolId, long yearId,
                                               long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Section>(code);
        }
        Section section = sectionPersistence.select(termId, sectionId);
        Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(sectionId);
        if(null != students && !students.isEmpty()) {
            section.setEnrolledStudents(new ArrayList<Student>(students));
        }
        Collection<Assignment> assignments = pm.getAssignmentPersistence().selectAll(sectionId);
        if(null != assignments && !assignments.isEmpty()) {
            section.setAssignments(new ArrayList<Assignment>(assignments));
        }
        return new ServiceResponse<Section>(section);
    }

    /**
     * Creates a section including enrolling any students in the enrolled student
     * set provided as a member of the Section instance provided.  Adding
     *  section assignments via this endpoint is not supported by this method
     *  at this time.  Any assignments on the instance provided will be ignored.
     *
     * @param schoolId
     * @param yearId
     * @param termId
     * @param section
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ServiceResponse<Long> createSection(long schoolId, long yearId,
                                               long termId, Section section) throws JsonProcessingException {
        //TODO: add support for creating section assignments that are on the provided instance
        StatusCode code = pm.getTermManager().termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long sectionId = sectionPersistence.insert(termId, section);
        if(null != section.getEnrolledStudents()) {
            for(Student s : section.getEnrolledStudents()) {
                if(null != s.getId() &&
                        pm.getStudentManager().studentExists(s.getId()).isOK()) {
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    pm.getStudentSectionGradePersistence().insert(sectionId, s.getId(), ssg);
                } else {
                    sectionPersistence.delete(sectionId);
                    throw new RuntimeException("Invalid section contained enrolled students that don't exist");
                    //TODO: return error to user and remove this exception
                }
            }
        }
        return new ServiceResponse<Long>(sectionId);
    }

    /**
     * When a section is updated, we are able to update the list of enrolled students, but we do
     * support updating the list of assignments in the section via this method at this time.
     * For that ADD/UPDATE/DELETE assignment API's can be called.
     *
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @param section
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ServiceResponse<Long> replaceSection(long schoolId, long yearId,
                                                long termId, long sectionId, Section section) throws JsonProcessingException {
        //TODO: add support for replacing section assignments that are provided on the instance.
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(sectionId);
        HashSet<Long> studentsInSection = new HashSet<>();
        for(Student s : students) {
            studentsInSection.add(s.getId());
        }
        if(null != section.getEnrolledStudents()) {
            for(Student s : section.getEnrolledStudents()) {
                if(!studentsInSection.contains(s.getId())) {
                    if(pm.getStudentManager().studentExists(s.getId()).isOK()) {
                        //Add existing student to the section
                        pm.getStudentSectionGradePersistence().insert(
                                sectionId, s.getId(), new StudentSectionGrade());
                    }
                } else {
                    //If the student was already in the section, remove it from the set and do nothing to the DB
                    studentsInSection.remove(s.getId());
                }
            }
        }
        //Remove any students that are no longer in the section
        for(Long id : studentsInSection) {
            pm.getStudentSectionGradePersistence().delete(sectionId, id);
        }
        sectionPersistence.update(termId, sectionId, section);
        return new ServiceResponse<Long>(sectionId);
    }

    @Override
    public ServiceResponse<Long> updateSection(long schoolId, long yearId,
                                               long termId, long sectionId, Section partialSection) throws JsonProcessingException {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        partialSection.mergePropertiesIfNull(sectionPersistence.select(termId, sectionId));
        if(null == partialSection.getEnrolledStudents() || partialSection.getEnrolledStudents().isEmpty()) {
            sectionPersistence.update(termId, sectionId, partialSection);
            return new ServiceResponse<Long>(sectionId);
        } else {
            replaceSection(schoolId, yearId, termId, sectionId, partialSection);
            return new ServiceResponse<Long>(sectionId);
        }
    }

    /**
     * Deleting a section will remove it from the system along with the studentSectionGrade instances
     * associated with the section and the assignments from the section.  This is accomplished in this
     * implementation via foreign key constraints with CASCADE DELETE turned on.
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @return
     */
    @Override
    public ServiceResponse<Long> deleteSection(long schoolId, long yearId,
                                               long termId, long sectionId) {
        StatusCode code = sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        sectionPersistence.delete(sectionId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Section>> getAllSectionsByTeacher(
            long schoolId, long yearId, long termId, long teacherId) {
        StatusCode code = pm.getTeacherManager().teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        code = pm.getTermManager().termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Section>>(code);
        }
        Collection<Section> sections = sectionPersistence.selectAllSectionForTeacher(termId, teacherId);
        for(Section s : sections) {
            Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(s.getId());
            if(null != students && !students.isEmpty()) {
                s.setEnrolledStudents(new ArrayList<Student>(students));
            }
            Collection<Assignment> assignments = pm.getAssignmentPersistence().selectAll(s.getId());
            if(null != assignments && !assignments.isEmpty()) {
                s.setAssignments(new ArrayList<Assignment>(assignments));
            }
        }
        return new ServiceResponse<Collection<Section>>(sections);
    }

}
