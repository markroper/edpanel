package com.scholarscore.etl.powerschool.sync.section;

import com.google.common.collect.BiMap;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetup;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeighting;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeightingWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtTerm;
import com.scholarscore.etl.powerschool.api.model.section.PtTermWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.api.response.PsSectionResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SyncBase;
import com.scholarscore.etl.powerschool.sync.assignment.SectionAssignmentSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For a single school within a district, this runnable can execute on its own thread and handles
 * extracting all sections for the school from PowerSchool and creating corresponding section in EdPanel.
 * Migration of a section includes enrolling the correct students, associating the correct teacher with the section,
 * migrating all the assignments for the section, migrating any student grades at the section or the assignment level.
 *
 * Created by markroper on 10/25/15.
 */
public class SectionSyncRunnable extends SyncBase<Section> implements Runnable, ISync<Section> {
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
    private Map<Long, Long> sectionPublicIdToSectionRecordId;
    private PowerSchoolSyncResult results;
    private Map<Long, Set<Section>> studentClasses;

    public SectionSyncRunnable(IPowerSchoolClient powerSchool,
                               IAPIClient edPanel,
                               School school,
                               ConcurrentHashMap<Long, Course> courses,
                               ConcurrentHashMap<Long, Term> terms,
                               StaffAssociator staffAssociator,
                               StudentAssociator studentAssociator,
                               ConcurrentHashMap<Long, Section> sections,   // this is for output -- results are added to this
                               Map<Long, Map<Long, PsFinalGradeSetup>> sectionIdToGradeFormula,
                               Map<Long, String> powerTeacherCategoryToEdPanelType,
                               BiMap<Long, Long> ptSectionIdToPsSectionId,
                               Map<Long, Long> ptStudentIdToPsStudentId,
                               Map<Long, Long> sectionPublicIdToSectionRecordId,
                               PowerSchoolSyncResult results,               // this is for output -- results are added to this
                               Map<Long, Set<Section>> studentClasses) {
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
        this.sectionPublicIdToSectionRecordId = sectionPublicIdToSectionRecordId;
        this.results = results;
        this.studentClasses = studentClasses;
    }

    @Override
    public void run() {
        syncCreateUpdateDelete(results);
    }

    @Override
    protected ConcurrentHashMap<Long, Section> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, Section> result = new ConcurrentHashMap<>();
        PsSectionResponse sr = powerSchool.getSectionsBySchoolId(Long.valueOf(school.getSourceSystemId()));
        if(null != sr && null != sr.sections && null != sr.sections.section) {
            List<PsSection> powerSchoolSections
                    = sr.sections.section;
            for (PsSection powerSection : powerSchoolSections) {
                Section edpanelSection = new Section();

                edpanelSection.setExpression(PsSection.evaluateExpression(powerSection.getExpression()));
                edpanelSection.setSourceSystemId(powerSection.getId().toString());
                //Resolve the EdPanel Course and set it on the EdPanel section
                Course c = this.courses.get(Long.valueOf(powerSection.getCourse_id()));
                if(null != c) {
                    edpanelSection.setCourse(c);
                    edpanelSection.setName(c.getName() + " " + powerSection.getExpression());
                }
                //Resolve the EdPanel Term and set it on the Section
                Term sectionTerm = this.terms.get(powerSection.getTerm_id());
                edpanelSection.setTerm(sectionTerm);
                edpanelSection.setStartDate(sectionTerm.getStartDate());
                edpanelSection.setEndDate(sectionTerm.getEndDate());
                //Resolve the EdPanel Teacher(s) and set on the Section
                User t = staffAssociator.findByUserSourceSystemId(powerSection.getStaff_id());
                if(null != t) {
                    HashSet<Staff> persons = new HashSet<>();
                    persons.add((Staff) t);
                    edpanelSection.setTeachers(persons);
                }
                edpanelSection.setGradeFormula(resolveSectionGradeFormula(powerSection));
                result.put(powerSection.getId(), edpanelSection);
            }
        }
        return result;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Failed to retrieve sections from PowerSchool for the school " +
                school.getName() + ", with ID: " + school.getId());
        results.sectionSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    private GradeFormula resolveSectionGradeFormula(PsSection powerSection) throws HttpClientException {
        //If there is a formula other than using assignment points and weights to calculate the grade,
        //Resolve that formula and set it on the section.
        if(null != powerSection.getId() && sectionIdToGradeFormula.containsKey(powerSection.getId())) {
            HashMap<Long, GradeFormula> allSectionFormulas = new HashMap<>();
            for (Map.Entry<Long, PsFinalGradeSetup> setupEntry : sectionIdToGradeFormula.get(powerSection.getId()).entrySet()) {
                GradeFormula gradeFormula = new GradeFormula();
                PsFinalGradeSetup setup = setupEntry.getValue();
                gradeFormula.setId(setupEntry.getKey());
                gradeFormula.setSourceSystemDescription(setup.finalgradesetuptype);
                gradeFormula.setLowScoreToDiscard(setup.lowscorestodiscard);
                //Get the term so we can set the term date ranges on the formula & set the formula ID to the powerschool term id
                PsResponse<PtTermWrapper> powerTeacherTermResponse =
                        powerSchool.getPowerTeacherTerm(setupEntry.getKey());
                if (null != powerTeacherTermResponse && powerTeacherTermResponse.record.size() > 0) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    PtTerm term = powerTeacherTermResponse.record.get(0).tables.psm_reportingterm;
                    String startDate = term.startdate;
                    String endDate = term.enddate;
                    gradeFormula.setParentId(term.parentreportingtermid);
                    gradeFormula.setName(term.name);
                    try {
                        gradeFormula.setStartDate(LocalDate.parse(startDate));
                        gradeFormula.setEndDate(LocalDate.parse(endDate));
                    } catch (Exception e) {
                        LOGGER.warn("Unable to parse start/end date for grading formula: " + e.getMessage());
                    }
                }
                //Now if the powerschool grade setup has a formula ID, we need to resolve the weights for that formula
                //and set them on the EdPanel GradeFormula instance
                if (null != setup.gradingformulaid && !setup.gradingformulaid.equals(0L)) {
                    PsResponse<PsSectionGradeFormulaWeightingWrapper> formulaWeightResponse =
                            powerSchool.getGradeFormulaWeights(setup.gradingformulaid);
                    Map<Long, Double> assignmentIdToPoints = new HashMap<>();
                    Map<String, Double> assignmentTypeToPoints = new HashMap<>();
                    for (PsResponseInner<PsSectionGradeFormulaWeightingWrapper> psweightwrapper :
                            formulaWeightResponse.record) {
                        //Powerschool supports assignment category weights and specific assignment weights
                        PsSectionGradeFormulaWeighting psWeight = psweightwrapper.tables.psm_gradingformulaweighting;
                        if (null != psWeight.assignmentcategoryid && !psWeight.assignmentcategoryid.equals(0L)) {
                            assignmentTypeToPoints.put(
                                    powerTeacherCategoryToEdPanelType.get(psWeight.assignmentcategoryid),
                                    psWeight.weighting);
                        } else if (null != psWeight.assignmentid && !psWeight.assignmentid.equals(0L)) {
                            assignmentIdToPoints.put(
                                    psWeight.assignmentid,
                                    psWeight.weighting);
                        }
                    }
                    gradeFormula.setAssignmentTypeWeights(assignmentTypeToPoints);
                    gradeFormula.setAssignmentWeights(assignmentIdToPoints);
                }

                //Put the formula in the map, and add it as a child to the parent
                if (allSectionFormulas.containsKey(gradeFormula.getParentId())) {
                    allSectionFormulas.get(gradeFormula.getParentId()).getChildren().add(gradeFormula);
                } else if (null != gradeFormula.getParentId()) {
                    GradeFormula parent = new GradeFormula();
                    parent.setId(gradeFormula.getParentId());
                    parent.setChildren(new HashSet<GradeFormula>() {{
                        add(gradeFormula);
                    }});
                    allSectionFormulas.put(gradeFormula.getParentId(), parent);
                }
                if (allSectionFormulas.containsKey(gradeFormula.getId())) {
                    GradeFormula imposter = allSectionFormulas.get(gradeFormula.getId());
                    gradeFormula.setChildren(imposter.getChildren());
                }
                allSectionFormulas.put(gradeFormula.getId(), gradeFormula);
            }
            //Find the root formula and return it
            for (Map.Entry<Long, GradeFormula> longGradeFormulaEntry : allSectionFormulas.entrySet()) {
                GradeFormula formula = longGradeFormulaEntry.getValue();
                if (null == formula.getParentId() || formula.getParentId().equals(0L)) {
                    return formula;
                }
            }
        }
        return null;
    }
    
    @Override
    protected ConcurrentHashMap<Long, Section> resolveFromEdPanel() throws HttpClientException {
        Collection<Section> sections = edPanel.getSections(school.getId());
        ConcurrentHashMap<Long, Section> sectionMap = new ConcurrentHashMap<>();
        for(Section s : sections) {
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                sectionMap.put(Long.valueOf(ssid), s);
            }
        }
        return sectionMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Failed to retrieve sections from EdPanel for the school " +
                school.getName() + ", with ID: " + school.getId());
        results.sectionEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected void createEdPanelRecord(Section entityToSave, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToSave.getSourceSystemId());
        Section created = null;
        try {
            created = edPanel.createSection(
                    school.getId(),
                    entityToSave.getTerm().getSchoolYear().getId(),
                    entityToSave.getTerm().getId(),
                    entityToSave);
        } catch (HttpClientException e) {
            results.sectionCreateFailed(ssid);
            LOGGER.info("Failed to create section...");
            return;
        }
        entityToSave.setId(created.getId());
        results.sectionCreated(ssid, entityToSave.getId());
        this.sections.put(ssid, entityToSave);
    }

    @Override
    protected void updateEdPanelRecord(Section sourceSystemEntity, Section edPanelEntity, PowerSchoolSyncResult results) {
        sourceSystemEntity.setId(edPanelEntity.getId());
        Long ssid = Long.valueOf(sourceSystemEntity.getSourceSystemId());
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                edPanel.replaceSection(school.getId(),
                        sourceSystemEntity.getTerm().getSchoolYear().getId(),
                        sourceSystemEntity.getTerm().getId(),
                        sourceSystemEntity);
            } catch (HttpClientException e) {
                LOGGER.info("Failed to update section...");
                results.sectionUpdateFailed(ssid, sourceSystemEntity.getId());
            }
            this.sections.put(ssid, sourceSystemEntity);
            results.sectionUpdated(ssid, sourceSystemEntity.getId());
        } else {
            // records are the same! need to record sameness
            results.sectionUntouched(ssid, sourceSystemEntity.getId());
        }
    }

    @Override
    protected void deleteEdPanelRecord(Section entityToDelete, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToDelete.getSourceSystemId());
        try {
            edPanel.deleteSection(school.getId(),
                    entityToDelete.getTerm().getSchoolYear().getId(),
                    entityToDelete.getTerm().getId(),
                    entityToDelete);
            results.sectionDeleted(ssid, entityToDelete.getId());
        } catch (HttpClientException e) {
            results.sectionDeleteFailed(ssid, entityToDelete.getId());
        }
    }

    @Override
    protected void entitySynced(Section sourceRecord, PowerSchoolSyncResult results) {
        StudentSectionGradeSync ssgSync = new StudentSectionGradeSync(
                powerSchool,
                edPanel,
                school,
                studentAssociator,
                ptSectionIdToPsSectionId,
                ptStudentIdToPsStudentId,
                sourceRecord,
                studentClasses);
        ssgSync.syncCreateUpdateDelete(results);

        SectionAssignmentSync assignmentSync = new SectionAssignmentSync(
                powerSchool,
                edPanel,
                school,
                studentAssociator,
                sectionPublicIdToSectionRecordId,
                sourceRecord
        );
        assignmentSync.syncCreateUpdateDelete(results);
        LOGGER.trace("Section, including assignments and student section grades created/updated. Section ID: " +
                sourceRecord.getId() + ", school ID: " + school.getId());
    }

    @Override
    protected void allEntitiesSynced(PowerSchoolSyncResult results) {
        LOGGER.info("All sections created and updated in EdPanel for school " + school.getName() +
                " with ID " + school.getId());
    }
}
