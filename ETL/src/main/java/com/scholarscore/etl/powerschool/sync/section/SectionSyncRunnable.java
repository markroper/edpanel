package com.scholarscore.etl.powerschool.sync.section;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.assignment.SectionAssignmentSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For a single school within a district, this runnable can execute on its own thread and handles
 * extracting all sections for the school from PowerSchool and creating corresponding section in EdPanel.
 * Migration of a section includes enrolling the correct students, associating the correct teacher with the section,
 * migrating all the assignments for the section, migrating any student grades at the section or the assignment level.
 *
 * Created by markroper on 10/25/15.
 */
public class SectionSyncRunnable implements Runnable, ISync<Section> {
    private static final int THREAD_POOL_SIZE = 10;
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private ConcurrentHashMap<Long, Section> sections;
    private List<Long> unresolvablePowerStudents;

    public SectionSyncRunnable(IPowerSchoolClient powerSchool,
                               IAPIClient edPanel,
                               School school,
                               ConcurrentHashMap<Long, Course> courses,
                               ConcurrentHashMap<Long, Term> terms,
                               StaffAssociator staffAssociator,
                               StudentAssociator studentAssociator,
                               ConcurrentHashMap<Long, Section> sections,
                               List<Long> unresolvablePowerStudents) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.courses = courses;
        this.terms = terms;
        this.staffAssociator = staffAssociator;
        this.studentAssociator = studentAssociator;
        this.sections = sections;
        this.unresolvablePowerStudents = unresolvablePowerStudents;
    }

    @Override
    public void run() {
        synchCreateUpdateDelete();
    }

    @Override
    public ConcurrentHashMap<Long, Section> synchCreateUpdateDelete() {
        ConcurrentHashMap<Long, Section> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Section> ed = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Section>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Section> entry = sourceIterator.next();
            Section sourceSection = entry.getValue();
            Section edPanelSection = ed.get(entry.getKey());
            if(null == edPanelSection){
                Section created = edPanel.createSection(
                       school.getId(),
                        sourceSection.getTerm().getSchoolYear().getId(),
                        sourceSection.getTerm().getId(),
                        sourceSection);
                sourceSection.setId(created.getId());
                this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
            } else {
                sourceSection.setId(edPanelSection.getId());
                if(!edPanelSection.equals(sourceSection)) {
                    edPanel.replaceSection(school.getId(),
                            sourceSection.getTerm().getSchoolYear().getId(),
                            sourceSection.getTerm().getId(),
                            sourceSection);
                    this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
                }
            }
            StudentSectionGradeSync ssgSync = new StudentSectionGradeSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    unresolvablePowerStudents,
                    sourceSection);
            ssgSync.synchCreateUpdateDelete();

            SectionAssignmentSync assignmentSync = new SectionAssignmentSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    unresolvablePowerStudents,
                    sourceSection
            );
            assignmentSync.synchCreateUpdateDelete();
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Section>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Section> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                Section edPanelSection = entry.getValue();
                edPanel.deleteSection(school.getId(),
                        edPanelSection.getTerm().getSchoolYear().getId(),
                        edPanelSection.getTerm().getId(),
                        edPanelSection);
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Section> resolveAllFromSourceSystem() {
        ConcurrentHashMap<Long, Section> result = new ConcurrentHashMap<>();
        SectionResponse sr = powerSchool.getSectionsBySchoolId(Long.valueOf(school.getSourceSystemId()));
        if(null != sr && null != sr.sections && null != sr.sections.section) {
            List<PsSection> powerSchoolSections
                    = sr.sections.section;
            for (PsSection powerSection : powerSchoolSections) {
                Section edpanelSection = new Section();
                edpanelSection.setSourceSystemId(powerSection.getId().toString());
                //Resolve the EdPanel Course and set it on the EdPanel section
                Course c = this.courses.get(Long.valueOf(powerSection.getCourse_id()));
                if(null != c) {
                    edpanelSection.setCourse(c);
                    edpanelSection.setName(c.getName());
                }
                //Resolve the EdPanel Term and set it on the Section
                Term sectionTerm = this.terms.get(powerSection.getTerm_id());
                edpanelSection.setTerm(sectionTerm);
                edpanelSection.setStartDate(sectionTerm.getStartDate());
                edpanelSection.setEndDate(sectionTerm.getEndDate());
                //Resolve the EdPanel Teacher(s) and set on the Section
                User t = staffAssociator.findBySourceSystemId(powerSection.getStaff_id());
                if(null != t && t instanceof Teacher) {
                    HashSet<Teacher> teachers = new HashSet<>();
                    teachers.add((Teacher) t);
                    edpanelSection.setTeachers(teachers);
                }
                result.put(powerSection.getId(), edpanelSection);
            }
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Section> resolveFromEdPanel() {
        Section[] sections = edPanel.getSections(school.getId());
        ConcurrentHashMap<Long, Section> sectionMap = new ConcurrentHashMap<>();
        for(Section s : sections) {
            Long id = null;
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                sectionMap.put(id, s);
            }
        }
        return sectionMap;
    }
}
