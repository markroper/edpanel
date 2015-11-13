package com.scholarscore.etl.powerschool.sync.section;

import com.google.common.collect.BiMap;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetup;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeighting;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeightingWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtTerm;
import com.scholarscore.etl.powerschool.api.model.section.PtTermWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.assignment.SectionAssignmentSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionSyncRunnable.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private ConcurrentHashMap<Long, Section> sections;
    private Map<Long, Map<Long, PsFinalGradeSetup>> sectionIdToGradeFormula;
    private Map<Long, String> powerTeacherCategoryToEdPanelType;
    private BiMap<Long, Long> ptSectionIdToPsSectionId;
    private Map<Long, Long> ptStudentIdToPsStudentId;
    private SyncResult results;

    public SectionSyncRunnable(IPowerSchoolClient powerSchool,
                               IAPIClient edPanel,
                               School school,
                               ConcurrentHashMap<Long, Course> courses,
                               ConcurrentHashMap<Long, Term> terms,
                               StaffAssociator staffAssociator,
                               StudentAssociator studentAssociator,
                               ConcurrentHashMap<Long, Section> sections,
                               Map<Long, Map<Long, PsFinalGradeSetup>> sectionIdToGradeFormula,
                               Map<Long, String> powerTeacherCategoryToEdPanelType,
                               BiMap<Long, Long> ptSectionIdToPsSectionId,
                               Map<Long, Long> ptStudentIdToPsStudentId,
                               SyncResult results) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.courses = courses;
        this.terms = terms;
        this.staffAssociator = staffAssociator;
        this.studentAssociator = studentAssociator;
        this.sections = sections;
        this.sectionIdToGradeFormula = sectionIdToGradeFormula;
        this.powerTeacherCategoryToEdPanelType = powerTeacherCategoryToEdPanelType;
        this.ptSectionIdToPsSectionId = ptSectionIdToPsSectionId;
        this.ptStudentIdToPsStudentId = ptStudentIdToPsStudentId;
        this.results = results;
    }

    @Override
    public void run() {
        syncCreateUpdateDelete(results);
    }

    @Override
    public ConcurrentHashMap<Long, Section> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Long, Section> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            results.sectionSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, Section> ed = null;
        try {
            ed = resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.sectionEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        Iterator<Map.Entry<Long, Section>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Section> entry = sourceIterator.next();
            Section sourceSection = entry.getValue();
            Section edPanelSection = ed.get(entry.getKey());
            if(null == edPanelSection){
                Section created = null;
                try {
                    created = edPanel.createSection(
                           school.getId(),
                            sourceSection.getTerm().getSchoolYear().getId(),
                            sourceSection.getTerm().getId(),
                            sourceSection);
                } catch (HttpClientException e) {
                    results.sectionCreateFailed(entry.getKey());
                    continue;
                }
                sourceSection.setId(created.getId());
                results.sectionCreated(entry.getKey(), sourceSection.getId());
                this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
            } else {
                sourceSection.setId(edPanelSection.getId());
                if(!edPanelSection.equals(sourceSection)) {
                    try {
                        edPanel.replaceSection(school.getId(),
                                sourceSection.getTerm().getSchoolYear().getId(),
                                sourceSection.getTerm().getId(),
                                sourceSection);
                    } catch (IOException e) {
                        results.sectionUpdateFailed(entry.getKey(), sourceSection.getId());
                        continue;
                    }
                    this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
                    results.sectionUpdated(entry.getKey(), sourceSection.getId());
                }
            }
            StudentSectionGradeSync ssgSync = new StudentSectionGradeSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    ptSectionIdToPsSectionId,
                    ptStudentIdToPsStudentId,
                    sourceSection);
            ssgSync.syncCreateUpdateDelete(results);

            SectionAssignmentSync assignmentSync = new SectionAssignmentSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    sourceSection
            );
            assignmentSync.syncCreateUpdateDelete(results);
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Section>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Section> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                Section edPanelSection = entry.getValue();
                try {
                    edPanel.deleteSection(school.getId(),
                            edPanelSection.getTerm().getSchoolYear().getId(),
                            edPanelSection.getTerm().getId(),
                            edPanelSection);
                } catch (HttpClientException e) {
                    results.sectionDeleteFailed(entry.getKey(), edPanelSection.getId());
                    continue;
                }
                results.sectionDeleted(entry.getKey(), edPanelSection.getId());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Section> resolveAllFromSourceSystem() throws HttpClientException {
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
                edpanelSection.setGradeFormula(resolveSectionGradeFormula(powerSection));
                result.put(powerSection.getId(), edpanelSection);
            }
        }
        return result;
    }

    protected GradeFormula resolveSectionGradeFormula(PsSection powerSection) throws HttpClientException {
        //If there is a formula other than using assignment points and weights to calculate the grade,
        //Resolve that formula and set it on the section.
        if(null != powerSection.getId() && sectionIdToGradeFormula.containsKey(powerSection.getId())) {
            Iterator<Map.Entry<Long, PsFinalGradeSetup>> it =
                    sectionIdToGradeFormula.get(powerSection.getId()).entrySet().iterator();
            HashMap<Long, GradeFormula> allSectionFormulas = new HashMap<>();
            while(it.hasNext()) {
                Map.Entry<Long, PsFinalGradeSetup> setupEntry = it.next();
                GradeFormula gradeFormula = new GradeFormula();
                PsFinalGradeSetup setup = setupEntry.getValue();
                gradeFormula.setId(setupEntry.getKey());
                gradeFormula.setSourceSystemDescription(setup.finalgradesetuptype);
                gradeFormula.setLowScoreToDiscard(setup.lowscorestodiscard);
                //Get the term so we can set the term date ranges on the formula & set the formula ID to the powerschool term id
                PsResponse<PtTermWrapper> powerTeacherTermResponse =
                        powerSchool.getPowerTeacherTerm(setupEntry.getKey());
                if(null != powerTeacherTermResponse && powerTeacherTermResponse.record.size() > 0) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    PtTerm term = powerTeacherTermResponse.record.get(0).tables.psm_reportingterm;
                    String startDate = term.startdate;
                    String endDate = term.enddate;
                    gradeFormula.setParentId(term.parentreportingtermid);
                    gradeFormula.setName(term.name);
                    try {
                        gradeFormula.setStartDate(df.parse(startDate));
                        gradeFormula.setEndDate(df.parse(endDate));
                    } catch (ParseException e) {
                        LOGGER.warn("Unable to parse start/end date for grading formula: " + e.getMessage());
                    }
                }
                //Now if the powerschool grade setup has a formula ID, we need to resolve the weights for that formula
                //and set them on the EdPanel GradeFormula instance
                if(null != setup.gradingformulaid && !setup.gradingformulaid.equals(0L)) {
                    PsResponse<PsSectionGradeFormulaWeightingWrapper> formulaWeightResponse =
                            powerSchool.getGradeFormulaWeights(setup.gradingformulaid);
                    Map<Long, Double> assignmentIdToPoints = new HashMap<>();
                    Map<String, Double> assignmentTypeToPoints = new HashMap<>();
                    for (PsResponseInner<PsSectionGradeFormulaWeightingWrapper> psweightwrapper :
                            formulaWeightResponse.record) {
                        //Powerschool supports assignment category weights and specific assignment weights
                        PsSectionGradeFormulaWeighting psWeight = psweightwrapper.tables.psm_gradingformulaweighting;
                        if(null != psWeight.assignmentcategoryid && !psWeight.assignmentcategoryid.equals(0L)) {
                            assignmentTypeToPoints.put(
                                    powerTeacherCategoryToEdPanelType.get(psWeight.assignmentcategoryid),
                                    psWeight.weighting);
                        } else if(null != psWeight.assignmentid && !psWeight.assignmentid.equals(0L)) {
                            assignmentIdToPoints.put(
                                    psWeight.assignmentid,
                                    psWeight.weighting);
                        }
                    }
                    gradeFormula.setAssignmentTypeWeights(assignmentTypeToPoints);
                    gradeFormula.setAssignmentWeights(assignmentIdToPoints);
                }

                //Put the formula in the map, and add it as a child to the parent
                if(allSectionFormulas.containsKey(gradeFormula.getParentId())) {
                    allSectionFormulas.get(gradeFormula.getParentId()).getChildren().add(gradeFormula);
                } else if(null != gradeFormula.getParentId()){
                    GradeFormula parent = new GradeFormula();
                    parent.setId(gradeFormula.getParentId());
                    parent.setChildren(new HashSet<GradeFormula>(){{ add(gradeFormula); }});
                    allSectionFormulas.put(gradeFormula.getParentId(), parent);
                }
                if(allSectionFormulas.containsKey(gradeFormula.getId())) {
                    GradeFormula imposter = allSectionFormulas.get(gradeFormula.getId());
                    gradeFormula.setChildren(imposter.getChildren());
                }
                allSectionFormulas.put(gradeFormula.getId(), gradeFormula);
            }
            //Find the root formula and return it
            Iterator<Map.Entry<Long, GradeFormula>> resultsIt = allSectionFormulas.entrySet().iterator();
            while(resultsIt.hasNext()) {
                GradeFormula formula = resultsIt.next().getValue();
                if(null == formula.getParentId() || formula.getParentId().equals(0L)) {
                    return formula;
                }
            }
        }
        return null;
    }
    protected ConcurrentHashMap<Long, Section> resolveFromEdPanel() throws HttpClientException {
        Section[] sections = edPanel.getSections(school.getId());
        ConcurrentHashMap<Long, Section> sectionMap = new ConcurrentHashMap<>();
        for(Section s : sections) {
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                Long id = Long.valueOf(ssid);
                sectionMap.put(id, s);
            }
        }
        return sectionMap;
    }
}
